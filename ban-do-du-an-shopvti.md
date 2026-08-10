# ShopVTI — Bản đồ dự án (dành cho AI đọc để tiếp tục sửa code)

Tài liệu này KHÔNG phải để trình bày — mục đích là để dán vào 1 AI khác (hoặc chính bạn) trước khi yêu cầu sửa/thêm chức năng, để AI hiểu ngay kiến trúc, quy ước, và **biết chính xác cần sửa file nào** mà không phải hỏi lại từ đầu.

---

## 0. Kiến trúc & quy ước bắt buộc phải biết trước khi sửa bất kỳ file nào

```
Frontend: React 18 (JavaScript thuần, KHÔNG TypeScript) + Vite + React Router v6
          + Axios + react-hook-form/yup + react-i18next + Tailwind CSS
          chạy tại http://localhost:5500

Backend:  Spring Boot 3.2.5, Java, package gốc com.vti
          Spring Security + JWT tự viết (KHÔNG dùng session)
          Spring Data JPA + MySQL, ModelMapper (Entity <-> DTO)
          chạy tại http://localhost:8080, mọi API dưới prefix /api
```

**5 quy ước xuyên suốt — VI PHẠM BẤT KỲ CÁI NÀO LÀ BUG:**

1. **User info KHÔNG bao giờ lưu localStorage** — chỉ lưu JWT token (`src/utils/storage.js` → `tokenStorage`). Thông tin user luôn nằm trong `AuthContext` (React state), khôi phục lại bằng gọi `GET /api/auth/me` mỗi lần app khởi động.
2. **`UserResponseDTO` (backend) chỉ có field `role` (chuỗi, số ít)**, KHÔNG PHẢI `roles` (mảng). Toàn bộ FE phải check bằng `user?.role === 'ADMIN'`. Đây là lỗi đã xảy ra thật (dùng nhầm `user?.roles?.includes('ADMIN')`) — nếu thấy chỗ nào còn dùng `roles` (số nhiều), đó là bug cần sửa theo đúng field `role`.
3. **Bảo mật CRUD sản phẩm/đơn hàng luôn 2 lớp, không được thiếu lớp nào:**
   - Lớp FE (`user?.role === 'ADMIN'`) — chỉ để **ẩn/hiện nút cho gọn UI**, KHÔNG PHẢI bảo mật thật.
   - Lớp BE (`@PreAuthorize("hasRole('ADMIN')")` trên Controller) — đây mới là chặn thật. Sửa quyền truy cập PHẢI sửa ở BE, sửa FE không đủ.
4. **Giá tiền/tên sản phẩm trong đơn hàng luôn lấy từ DB (`Product` entity) ở tầng `OrderService`, KHÔNG BAO GIỜ lấy từ request client gửi lên** — chống gian lận sửa giá qua DevTools/Postman.
5. **Mọi state lọc/phân trang sống trên URL (`useSearchParams`), không phải `useState` nội bộ** — để F5/back-forward/copy-link hoạt động đúng.

---

## 1. Cấu trúc thư mục Frontend (`src/`)

```
src/
├── api/                          ← TẦNG DUY NHẤT gọi axios trực tiếp
│   ├── axiosClient.js               instance Axios chung; request interceptor gắn
│   │                                 Authorization: Bearer <token>; response interceptor
│   │                                 bắt lỗi tập trung (401 → logout+redirect, khác → toast)
│   ├── authApi.js                   login, register, confirmEmail, getCurrentUser
│   ├── productApi.js                getAll (gọi /products/search, KHÔNG PHẢI /products —
│   │                                 xem mục 3 "bẫy đã gặp"), getById, create, update, remove
│   ├── orderApi.js                  create, getMyOrders (GET /orders), getAllOrders
│   │                                 (GET /orders/admin/all — admin), updateStatus
│   ├── paymentApi.js                createVnpayUrl(orderId) → POST /payments/vnpay/create-url
│   └── commentApi.js                getByProduct(productId), create(productId, data)
│
├── contexts/                     ← Global state, KHÔNG gọi axios trực tiếp (gọi qua api/*)
│   ├── AuthContext.jsx              user, isAuthenticated, isLoading, login(), logout()
│   │                                 login() trả về user vừa đăng nhập (để Login.jsx điều
│   │                                 hướng theo role)
│   └── CartContext.jsx              items, selectedIds, selectedItems (derived qua useMemo,
│                                     KHÔNG lưu riêng), addItem, updateQuantity, removeItem,
│                                     removeItems (xóa 1 phần — dùng sau khi checkout thành
│                                     công), toggleSelected, selectAll, clearSelection
│
├── hooks/
│   ├── useAuth.js                   đọc AuthContext, throw nếu dùng ngoài Provider
│   └── useCart.js                   đọc CartContext
│
├── routes/
│   ├── Layout.jsx                   Header + <Outlet/> + Footer — bọc MỌI trang
│   ├── RequireAuth.jsx              Tầng 1: chỉ hỏi "đã đăng nhập chưa" → chưa thì
│   │                                 <Navigate to="/login" state={{from:location}}/>
│   ├── RequireRole.jsx              Tầng 2 (lồng TRONG RequireAuth): check
│   │                                 user?.role trong allowedRoles → sai thì
│   │                                 <Navigate to="/403"/>
│   └── AppRoutes.jsx                Định nghĩa toàn bộ route — xem mục 2 để biết path nào
│                                     cần guard nào
│
├── pages/                        ← DUY NHẤT gọi useEffect + api/*; ghép component nhỏ lại
│   ├── HomePage.jsx                  hero + "Sản phẩm nổi bật" — CÓ phân trang riêng
│   │                                 (PAGE_SIZE=3, state page/totalPages nội bộ, KHÔNG
│   │                                 dùng URL vì đây không phải trang danh sách chính)
│   ├── Products.jsx                  danh sách công khai — filter+search+phân trang qua
│   │                                 URL (useSearchParams), PAGE_SIZE=3; nút "Thêm sản
│   │                                 phẩm" chỉ hiện nếu user?.role === 'ADMIN'
│   ├── ProductDetail.jsx             route động /products/:id (useParams); render
│   │                                 <ProductComments productId={product.id}/> ở cuối
│   ├── ProductForm.jsx               dùng CHUNG cho tạo (/admin/products/new) và sửa
│   │                                 (/admin/products/:id/edit) — isEdit = !!useParams().id;
│   │                                 field ảnh dùng <Controller> + ImageUploadInput
│   │                                 (base64), KHÔNG PHẢI input text URL
│   ├── Login.jsx                     sau login: nếu user.role==='ADMIN' → navigate('/admin'),
│   │                                 ngược lại → location.state.from hoặc '/'
│   ├── Register.jsx
│   ├── ConfirmEmail.jsx
│   ├── CartPage.jsx                  checkbox từng dòng + "chọn tất cả"; nút Đặt hàng
│   │                                 disable nếu selectedIds rỗng
│   ├── CheckoutPage.jsx              dùng selectedItems (không phải toàn bộ items);
│   │                                 submit thành công → removeItems() (xóa đúng phần đã
│   │                                 đặt); nếu paymentMethod==='VNPAY' → gọi
│   │                                 paymentApi.createVnpayUrl() → window.location.href =
│   │                                 paymentUrl (KHÔNG dùng navigate())
│   ├── PaymentResult.jsx             đọc ?status=&orderId= từ URL sau khi VNPay redirect về
│   ├── OrderHistory.jsx              GET /orders — BE tự lọc theo username từ token, FE
│   │                                 không cần tự lọc gì thêm
│   ├── Profile.jsx
│   ├── Forbidden.jsx                 trang 403 — vào khi RequireRole chặn
│   ├── NotFound.jsx                  trang 404 — route path="*"
│   └── admin/
│       ├── AdminDashboard.jsx        trang /admin — 2 thẻ link tới AdminProducts/AdminOrders
│       ├── AdminProducts.jsx         bảng sản phẩm + Sửa/Xóa, phân trang PAGE_SIZE=3,
│       │                             có link "← Về trang quản trị"
│       └── AdminOrders.jsx           danh sách đơn + <select> đổi status → PUT
│                                     /orders/{id}/status, có link "← Về trang quản trị"
│
├── components/                   ← UI thuần, KHÔNG tự gọi API, nhận data qua props
│   ├── common/
│   │   ├── Header.jsx                logo, nav (Trang chủ/Sản phẩm/[Đơn hàng nếu login]/
│   │   │                             [Quản trị nếu role===ADMIN]), LanguageSwitcher,
│   │   │                             icon giỏ hàng (badge = totalItems), user/Đăng xuất
│   │   ├── Footer.jsx
│   │   ├── LoadingSpinner.jsx
│   │   ├── LanguageSwitcher.jsx      gọi changeLanguage('vi'|'en') từ i18n/index.js
│   │   └── Pagination.jsx            props: page, totalPages, onPageChange — dùng chung
│   │                                 cho HomePage/Products/AdminProducts
│   ├── products/
│   │   ├── ProductCard.jsx           nút "Thêm vào giỏ" → addItem() + toast.success()
│   │   ├── ProductList.jsx           grid thuần, nhận products+isLoading qua props
│   │   ├── ProductFilter.jsx         form lọc — gọi callback onApply(params), KHÔNG tự
│   │   │                             gọi API (cha Products.jsx mới gọi)
│   │   ├── ImageUploadInput.jsx      <input type="file"> → FileReader → base64 string →
│   │   │                             onChange(base64) — dùng trong ProductForm qua Controller
│   │   └── ProductComments.jsx       list bình luận (ai cũng xem) + form thêm (chỉ hiện
│   │                                 nếu isAuthenticated)
│   └── orders/
│       ├── OrderItem.jsx
│       └── OrderList.jsx
│
├── utils/
│   ├── formatCurrency.js            formatCurrency(), formatDate()
│   ├── validate.js                  TOÀN BỘ yup schema: loginSchema, registerSchema,
│   │                                 productSchema, checkoutSchema
│   └── storage.js                   tokenStorage (get/set/clear), langStorage,
│                                     cartStorage — DUY NHẤT nơi được đụng localStorage
│
├── i18n/
│   ├── index.js                      init i18next, changeLanguage(), đọc/ghi langStorage
│   └── locales/{vi,en}.json           mọi chuỗi text — key namespace: nav, home, product,
│                                       cart, checkout, orders, auth, profile, validation,
│                                       common, admin, comments, payment
│
├── config.js                     API_BASE_URL (thay cho import.meta.env khi không có Vite;
│                                   bản hiện tại DÙNG Vite nên đọc qua .env là chuẩn — file
│                                   này chỉ tồn tại ở bản "no-bundler" đã bỏ)
├── App.jsx                       chỉ render <AppRoutes/>
└── main.jsx                      mount React, bọc <AuthProvider><CartProvider>
```

---

## 2. Cấu trúc route (`AppRoutes.jsx`) — path nào cần guard nào

```
/ (Layout — bọc mọi route)
├─ index                    HomePage           — công khai
├─ products                 Products           — công khai
├─ products/:id              ProductDetail      — công khai, route động
├─ login, register,
│  confirm-email             — công khai
├─ payment-result            PaymentResult      — công khai (VNPay redirect vào đây)
├─ 403                       Forbidden          — công khai
│
├─ [RequireAuth]            ← cần đăng nhập
│   ├─ cart                 CartPage
│   ├─ checkout             CheckoutPage
│   ├─ orders               OrderHistory
│   ├─ profile              Profile
│   │
│   └─ [RequireRole allowedRoles=['ADMIN']]   ← cần đăng nhập VÀ role ADMIN
│       ├─ admin                    AdminDashboard
│       ├─ admin/products           AdminProducts
│       ├─ admin/products/new       ProductForm (isEdit=false)
│       ├─ admin/products/:id/edit  ProductForm (isEdit=true)
│       └─ admin/orders             AdminOrders
│
└─ *  (catch-all)           NotFound — 404
```

---

## 3. Cấu trúc Backend (`com.vti`)

```
com/vti/
├── MyProjectVti12Application.java     entry point

├── config/
│   ├── SecurityConfig.java              PasswordEncoder(BCrypt), AuthenticationProvider,
│   │                                     SecurityFilterChain (rule permitAll/hasRole/
│   │                                     authenticated theo path+method), CORS,
│   │                                     addFilterBefore(JwtAuthenticationFilter,
│   │                                     UsernamePasswordAuthenticationFilter.class),
│   │                                     sessionCreationPolicy(STATELESS)
│   ├── VNPayConfig.java                 đọc vnpay.* từ application.properties
│   ├── ModelMapperConfig.java
│   └── CorsConfig.java                  (có thể dư thừa — CORS thật nằm trong
│                                         SecurityConfig.corsConfigurationSource())

├── security/
│   ├── JwtUtil.java                     generateToken(username), extractUsername(token),
│   │                                     isTokenValid(token, username)
│   └── JwtAuthenticationFilter.java     OncePerRequestFilter — chạy TRƯỚC mọi Controller,
│                                         đọc header Authorization, verify token, set
│                                         SecurityContextHolder nếu hợp lệ

├── controller/                        nhận HTTP, KHÔNG chứa business logic
│   ├── AuthController.java              POST /auth/login (trả {token, user}), /register,
│   │                                     GET /auth/me, POST /auth/logout
│   ├── ProductController.java           GET /products (KHÔNG filter — trả findAll thô),
│   │                                     GET /products/search?q&min&max (CÓ filter, dùng
│   │                                     Specification, @PageableDefault(size=3,sort="id")),
│   │                                     GET /products/{id}, POST/PUT/DELETE /products
│   │                                     (đều @PreAuthorize hasRole ADMIN)
│   ├── OrderController.java             POST /orders (tạo đơn), GET /orders (đơn của
│   │                                     CHÍNH user gọi — lọc theo Authentication.getName()),
│   │                                     GET /orders/admin/all (mọi đơn — cho admin),
│   │                                     PUT /orders/{id}/status
│   ├── PaymentController.java           POST /payments/vnpay/create-url,
│   │                                     GET /payments/vnpay/callback (VNPay redirect vào
│   │                                     đây, verify chữ ký HMAC-SHA512 rồi redirect tiếp
│   │                                     về FE /payment-result)
│   └── CommentController.java           GET /products/{productId}/comments (công khai),
│                                         POST /products/{productId}/comments (cần login,
│                                         username lấy từ Authentication, KHÔNG từ body)

├── dto/                                hình dạng request/response — KHÔNG PHẢI Entity
│   ├── LoginRequestDTO, RegisterRequestDTO, LoginResponseDTO, UserResponseDTO
│   ├── ProductDTO
│   ├── OrderRequestDTO (fields: items, shippingAddress, paymentMethod — ĐÃ SỬA từ
│   │   customerName/Email/Phone/Address cũ, xem mục "bẫy đã gặp" #4),
│   │   OrderItemRequestDTO, OrderResponseDTO
│   ├── CommentRequestDTO, CommentResponseDTO
│   └── ErrorResponse

├── entity/                             ánh xạ bảng MySQL
│   ├── User.java                        id, username, email, password(hash BCrypt),
│   │                                     fullName, role(String, SỐ ÍT), status, createdAt
│   ├── Product.java                     ..., imageUrl (@Lob columnDefinition="LONGTEXT" —
│   │                                     lưu base64, KHÔNG PHẢI VARCHAR nữa)
│   ├── Order.java                       ..., username (liên kết đơn↔user), customerName/
│   │                                     Email/Address/Phone (lấy từ User lúc tạo đơn,
│   │                                     KHÔNG từ request), paymentMethod, status,
│   │                                     vnpTxnRef (mã giao dịch VNPay) — CẦN getter/setter
│   │                                     đầy đủ, đã có lần thiếu gây lỗi compile
│   ├── OrderItem.java
│   ├── Comment.java                     productId, username(tác giả — từ token), content,
│   │                                     rating, createdAt
│   └── RegistrationUserToken.java

├── repository/                         Spring Data JPA — tên method quyết định SQL tự sinh
│   ├── UserRepository                   findByUsername, existsByUsername, existsByEmail
│   ├── ProductRepository
│   ├── OrderRepository                  findByUsernameOrderByOrderDateDesc(username) —
│   │                                     dùng cho "đơn hàng của tôi"
│   ├── OrderItemRepository              deleteByProductId (dùng khi xóa sản phẩm)
│   ├── CommentRepository                findByProductIdOrderByCreatedAtDesc
│   └── RegistrationUserTokenRepository

├── service/                            business logic thật — Controller chỉ gọi xuống đây
│   ├── UserService.java                 register() — hash password, set role="USER"
│   │                                     status="ACTIVE"; findByUsername()
│   ├── CustomUserDetailsService.java     implements UserDetailsService — load user cho
│   │                                     AuthenticationManager, check status=="ACTIVE",
│   │                                     .roles(user.getRole()) → Spring tự thêm tiền tố
│   │                                     ROLE_ (nên DB chỉ lưu "ADMIN"/"USER", KHÔNG lưu
│   │                                     sẵn "ROLE_ADMIN" — lưu vậy sẽ bị double-prefix)
│   ├── ProductService.java              getAllProducts (KHÔNG filter), searchProducts
│   │                                     (CÓ filter, dùng ProductSpecification), CRUD
│   ├── OrderService.java                createOrder() — @Transactional; lấy username từ
│   │                                     SecurityContextHolder; với MỖI item: tra Product
│   │                                     thật từ DB lấy giá+tên (KHÔNG tin request), check
│   │                                     đủ tồn kho, trừ kho; getMyOrders(username),
│   │                                     getAllOrders() (admin)
│   ├── CommentService.java              addComment() — username từ tham số truyền vào
│   │                                     (Controller lấy từ Authentication), KHÔNG từ DTO
│   └── EmailService.java

├── specification/
│   └── ProductSpecification.java        containsTextInNameOrDesc(search),
│                                         minPrice(min), maxPrice(max) — build
│                                         Specification<Product> động, kết hợp bằng .and()

├── util/
│   └── VNPayUtil.java                    hmacSHA512() ký/verify chữ ký, buildQuery()
│                                          (SẮP XẾP THEO ALPHABET key trước khi ký —
│                                          bắt buộc, sai thứ tự = sai chữ ký)

├── event/                              (đăng ký user — cơ chế bất đồng bộ, không dùng
│   ├── OnSendRegistrationUserConfirmViaEmailEvent.java   cho luồng chính hiện tại vì
│   └── SendRegistrationUserConfirmViaEmailListener.java  UserService.register() đã tự
│                                                          set status=ACTIVE ngay)

└── exception/
    ├── GlobalExceptionHandler.java      @RestControllerAdvice — bắt MỌI exception từ MỌI
    │                                     Controller: ResourceNotFoundException→404,
    │                                     BadRequestException→400,
    │                                     MethodArgumentNotValidException→400 (kèm list lỗi
    │                                     từng field), Exception còn lại→500 (ẩn chi tiết
    │                                     kỹ thuật khỏi client)
    ├── ResourceNotFoundException.java
    └── BadRequestException.java
```

---

## 4. BẢNG TRA CỨU — sửa chức năng X thì đụng đúng những file nào

| Chức năng | File Frontend cần sửa | File Backend cần sửa |
|---|---|---|
| Đăng ký | `Register.jsx`, `utils/validate.js` (registerSchema) | `AuthController`, `UserService`, `RegisterRequestDTO` |
| Đăng nhập / JWT | `Login.jsx`, `AuthContext.jsx`, `authApi.js` | `AuthController`, `SecurityConfig`, `security/JwtUtil.java`, `security/JwtAuthenticationFilter.java`, `CustomUserDetailsService` |
| Bảo vệ route theo đăng nhập | `routes/RequireAuth.jsx`, `AppRoutes.jsx` | — (chỉ FE) |
| Bảo vệ route/API theo role | `routes/RequireRole.jsx`, chỗ check `user?.role` ở `Header.jsx`/`Products.jsx` | `SecurityConfig` (`hasRole`), `@PreAuthorize` trên Controller tương ứng |
| Danh sách sản phẩm + lọc + phân trang | `Products.jsx`, `ProductFilter.jsx`, `productApi.js`, `Pagination.jsx` | `ProductController.searchProducts`, `ProductService.searchProducts`, `ProductSpecification` |
| Chi tiết sản phẩm | `ProductDetail.jsx` | `ProductController.getProductById`, `ProductService` |
| CRUD sản phẩm (admin) | `pages/admin/AdminProducts.jsx`, `ProductForm.jsx`, `ImageUploadInput.jsx`, `productApi.js` | `ProductController` (POST/PUT/DELETE), `ProductService`, `Product.java` (nếu đổi field) |
| Ảnh sản phẩm (base64) | `ImageUploadInput.jsx`, `ProductForm.jsx` | `Product.java` (`imageUrl` LONGTEXT), có thể cần `ALTER TABLE` tay trong MySQL |
| Giỏ hàng (thêm/sửa/xóa/chọn) | `CartContext.jsx`, `CartPage.jsx`, `utils/storage.js` (cartStorage) | — (giỏ hàng thuần client-side, không chạm BE) |
| Đặt hàng (checkout) | `CheckoutPage.jsx`, `orderApi.js`, `utils/validate.js` (checkoutSchema) | `OrderController.createOrder`, `OrderService.createOrder`, `OrderRequestDTO`, `Order.java`, `OrderItem.java` |
| Lịch sử đơn hàng (user) | `OrderHistory.jsx`, `OrderList.jsx`, `OrderItem.jsx` | `OrderController.getMyOrders`, `OrderService.getMyOrders`, `OrderRepository.findByUsernameOrderByOrderDateDesc` |
| Quản lý đơn hàng (admin) | `pages/admin/AdminOrders.jsx`, `orderApi.js` (getAllOrders, updateStatus) | `OrderController` (`/orders/admin/all`, `/orders/{id}/status`) |
| Thanh toán VNPay | `CheckoutPage.jsx` (nhánh `paymentMethod==='VNPAY'`), `paymentApi.js`, `PaymentResult.jsx` | `PaymentController`, `VNPayConfig`, `util/VNPayUtil.java`, `Order.java` (`vnpTxnRef`), `application.properties` (`vnpay.*`) |
| Bình luận/đánh giá | `ProductComments.jsx` (gắn trong `ProductDetail.jsx`), `commentApi.js` | `CommentController`, `CommentService`, `Comment.java`, `CommentRepository`, `SecurityConfig` (rule GET permitAll / POST authenticated) |
| Đổi ngôn ngữ | `LanguageSwitcher.jsx`, `i18n/index.js`, `i18n/locales/*.json` | — (không chạm BE) |
| Xử lý lỗi hiển thị cho user | `api/axiosClient.js` (response interceptor) | `exception/GlobalExceptionHandler.java` |
| Format tiền/ngày tháng | `utils/formatCurrency.js` | — |
| Validation form | `utils/validate.js` (yup schema tương ứng) | DTO tương ứng (`@Valid`, `@NotBlank`...) nếu cần validate cả 2 phía |

---

## 5. Bẫy/bug ĐÃ GẶP THẬT — đừng lặp lại

1. **`user?.roles?.includes('ADMIN')` sai** — `UserResponseDTO` chỉ có field `role` số ít. Luôn dùng `user?.role === 'ADMIN'`.
2. **`ProductController.searchProducts` thiếu `@PageableDefault(sort="id")`** → phân trang bị lệch/lặp trang do MySQL không đảm bảo thứ tự ổn định khi không có `ORDER BY` rõ ràng.
3. **`ProductController.getAllProducts` (`GET /products`, không có `/search`) KHÔNG áp dụng filter gì cả** — FE phải gọi đúng `GET /products/search?q=&min=&max=` (qua `productApi.getAll()`), không gọi `/products` nếu cần lọc.
4. **`OrderRequestDTO` từng có field `customerName/customerEmail/customerAddress/customerPhone`** — không khớp với những gì FE thực sự gửi (`shippingAddress`, `paymentMethod`). Đã sửa: DTO giờ chỉ có `items`, `shippingAddress`, `paymentMethod`; `customerName/Email` lấy từ `User` (qua username trong token) ở `OrderService`, không nhận từ client.
5. **`OrderItemRequestDTO.price` bị lấy trực tiếp từ request** (trước khi sửa) → NullPointerException vì FE không gửi giá. Đã sửa: `OrderService` luôn tra `Product.getPrice()` từ DB.
6. **`Order` không có field `username`** (trước khi sửa) → `GET /orders` trả về đơn của MỌI người dùng, không lọc theo ai đang đăng nhập. Đã sửa: thêm field `username`, lọc bằng `findByUsernameOrderByOrderDateDesc`.
7. **Constructor `AuthController` thiếu tham số `JwtUtil jwtUtil`** dù đã có field khai báo → lỗi compile "blank final field". Khi thêm field `final` mới vào bất kỳ Controller/Service nào, PHẢI thêm vào constructor, không dùng quick-fix "Initialize at declaration" (phá vỡ Dependency Injection).
8. **`Order.java` thiếu getter/setter cho `vnpTxnRef`** sau khi thêm field — luôn thêm đủ cặp getter/setter ngay khi thêm field mới vào entity dùng kiểu Java thuần (không Lombok).
9. **Đăng nhập ban đầu không sinh JWT thật** (code cũ chỉ set `SecurityContextHolder` trong 1 request rồi mất, không có token) → mọi request sau đó "vô danh". Bắt buộc phải có `JwtUtil` + `JwtAuthenticationFilter` + `SecurityConfig` set `SessionCreationPolicy.STATELESS`.
10. **`SecurityConfig` từng để `permitAll()` cho toàn bộ `/api/products/**` và `/api/orders/**`** — cho phép tạo/sửa/xóa sản phẩm mà không cần token nào. Rule đúng: GET công khai, POST/PUT/DELETE sản phẩm → `hasRole("ADMIN")`, mọi thứ `/api/orders/**` → `authenticated()`.

---

## 6. Cách dùng tài liệu này khi nhờ AI sửa code

Khi cần sửa/thêm 1 chức năng, đưa AI: **(a) file này**, **(b) nội dung file(s) liên quan tra ở Bảng mục 4**, **(c) mô tả cụ thể lỗi/yêu cầu kèm ảnh Console+Network nếu là bug**. Không cần đưa toàn bộ project — bảng tra cứu đã chỉ đúng phạm vi cần đụng tới cho từng chức năng.
