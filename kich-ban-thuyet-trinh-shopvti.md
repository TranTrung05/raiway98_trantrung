# Kịch bản thuyết trình — ShopVTI (theo đúng bảng đánh giá)

*Viết ở ngôi thứ nhất — dùng để đọc/luyện nói trực tiếp khi thuyết trình. Mỗi mục đi qua: luồng chạy → từng dòng code quan trọng → file nào liên kết với file nào.*

---

## Mở đầu

Em xin trình bày đồ án ShopVTI — một ứng dụng thương mại điện tử gồm 2 phần tách biệt: **Frontend** viết bằng React (JavaScript thuần, dùng Vite làm dev server/build tool) và **Backend** viết bằng Spring Boot (Java), giao tiếp với nhau qua REST API dạng JSON, xác thực bằng JWT. Em sẽ trình bày lần lượt theo đúng 11 tiêu chí trong bảng đánh giá, đi sâu vào luồng chạy thật của code, không chỉ liệt kê tính năng.

---

## 1. Giao diện và bố cục

Em thiết kế toàn bộ giao diện dùng chung 1 khung layout, tránh lặp code Header/Footer ở từng trang.

**File `src/routes/Layout.jsx`:**
```jsx
export default function Layout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-6">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
```
Em giải thích: `<Outlet />` là component đặc biệt của React Router — nó là **chỗ trống** để React Router tự động chèn đúng trang tương ứng với URL hiện tại vào. Nghĩa là dù người dùng đang ở `/products` hay `/cart`, `<Header />` và `<Footer />` luôn hiển thị y nguyên, chỉ phần `<Outlet />` ở giữa thay đổi nội dung. Cách làm này gọi là **layout route pattern**, được khai báo bên `AppRoutes.jsx`:
```jsx
<Route element={<Layout />}>
  <Route index element={<HomePage />} />
  <Route path="products" element={<Products />} />
  ...
</Route>
```
Mọi route con nằm bên trong `<Route element={<Layout />}>` đều tự động được bọc bởi Layout.

Về responsive, em dùng Tailwind CSS với hệ thống breakpoint theo kiểu **mobile-first**: class không có tiền tố áp dụng cho mọi màn hình, tiền tố `sm:`/`md:`/`lg:` chỉ áp dụng khi màn hình đủ rộng. Ví dụ trong `ProductList.jsx`:
```jsx
<div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
```
Trên điện thoại (mặc định) hiển thị 2 cột, màn hình `sm` trở lên (≥640px) thành 3 cột, `lg` trở lên (≥1024px) thành 4 cột — em không cần viết media query tay, Tailwind tự sinh CSS tương ứng.

Header có menu riêng cho mobile — em quản lý bằng 1 state boolean đơn giản trong `Header.jsx`:
```jsx
const [menuOpen, setMenuOpen] = useState(false);
```
Bấm icon hamburger → `setMenuOpen(!menuOpen)` → menu mobile render có điều kiện `{menuOpen && (...)}`.

---

## 2. Component, Props, State, Context

Đây là phần em muốn nhấn mạnh nhất vì đề bài yêu cầu rõ: *"khi đăng nhập thành công, lưu thông tin người dùng vào context thay vì dùng localStorage"*.

### Vì sao không lưu user info vào localStorage

localStorage không có cơ chế hết hạn, và bất kỳ đoạn JavaScript nào chạy được trên trang (kể cả từ 1 lỗ hổng XSS) đều đọc được toàn bộ nội dung trong đó. Nếu em lưu cả `username`, `role` vào localStorage, đó là thông tin nhạy cảm bị phơi ra không cần thiết. Giải pháp em chọn: **chỉ lưu duy nhất chuỗi JWT token** — bắt buộc phải lưu ở đâu đó để duy trì đăng nhập qua lần reload — còn **toàn bộ thông tin user luôn được hỏi lại từ server mỗi khi cần**.

**File `src/contexts/AuthContext.jsx`** — em đi qua từng phần:

```jsx
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
```
Em khai báo 2 state: `user` (object thông tin người dùng, ban đầu `null` — nghĩa là "chưa biết ai đang đăng nhập") và `isLoading` (đang trong lúc khôi phục phiên đăng nhập lúc app khởi động, ban đầu `true`).

```jsx
  const restoreSession = useCallback(async () => {
    const token = tokenStorage.get();
    if (!token) {
      setIsLoading(false);
      return;
    }
    try {
      const me = await authApi.getCurrentUser();
      setUser(me);
    } catch {
      tokenStorage.clear();
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    restoreSession();
  }, [restoreSession]);
```
Đây là đoạn quan trọng nhất — chạy đúng **1 lần** khi app khởi động (dependency array chỉ chứa `restoreSession`, và hàm này được bọc `useCallback` với mảng rỗng nên không đổi giữa các lần render, tức effect chỉ chạy 1 lần). Logic: đọc token từ localStorage qua `tokenStorage.get()` (hàm tiện ích em viết ở `utils/storage.js`) — nếu không có token, coi như chưa đăng nhập, dừng lại. Nếu có token, gọi `authApi.getCurrentUser()` — đây chính là lúc "hỏi lại server", không đọc gì thêm từ localStorage.

```jsx
  const login = useCallback(async (data) => {
    const res = await authApi.login(data);
    tokenStorage.set(res.token);
    const me = res.user ?? (await authApi.getCurrentUser());
    setUser(me);
    return me;
  }, []);
```
Hàm `login` gọi `authApi.login(data)` → nhận về `{ token, user }` → `tokenStorage.set(res.token)` **chỉ lưu token**, dòng `setUser(me)` đưa thông tin user vào **React state của Context**, không đụng gì đến localStorage. Em return `me` ở cuối để component gọi hàm này (là `Login.jsx`) biết được role của user vừa đăng nhập, dùng để điều hướng đúng chỗ.

### Component tái sử dụng qua Props

Em lấy ví dụ `ProductCard.jsx` nhận đúng 1 prop `product`:
```jsx
export default function ProductCard({ product }) {
  ...
  <button onClick={() => addItem(product, 1)}>Thêm vào giỏ</button>
}
```
Component này được `ProductList.jsx` gọi lặp lại nhiều lần:
```jsx
{products.map((p) => <ProductCard key={p.id} product={p} />)}
```
`ProductList.jsx` lại được dùng ở cả `HomePage.jsx` lẫn `Products.jsx` — chứng minh khả năng tái sử dụng: cùng 1 component hiển thị lưới sản phẩm, dùng ở 2 trang khác nhau, chỉ khác dữ liệu truyền vào qua prop `products`.

Em cũng có ví dụ về **callback props** — `ProductFilter.jsx`:
```jsx
export default function ProductFilter({ initial, onApply }) {
  ...
  const handleSubmit = (e) => {
    e.preventDefault();
    onApply({ name, minPrice, maxPrice });
  };
```
`ProductFilter` không tự gọi API — nó chỉ thu thập input người dùng gõ, rồi gọi `onApply(...)` — hàm này thực ra là do `Products.jsx` (component cha) truyền xuống:
```jsx
<ProductFilter initial={{...}} onApply={applyFilter} />
```
Đây đúng nguyên tắc của React: **dữ liệu đi xuống qua props, sự kiện đi lên qua callback** — component con không cần biết cha sẽ làm gì với dữ liệu đó (ở đây là đổi URL rồi gọi API), nó chỉ có nhiệm vụ báo "người dùng vừa bấm Lọc, đây là giá trị".

### 2 Context tách biệt

Em có `AuthContext` (ai đang đăng nhập) và `CartContext` (giỏ hàng đang có gì) — tách riêng vì giỏ hàng phải hoạt động được **ngay cả khi chưa đăng nhập** (khách vãng lai thêm hàng vào giỏ trước, bắt đăng nhập lúc thanh toán mới đúng trải nghiệm mua sắm thực tế). Gộp chung 2 Context sẽ vi phạm nguyên tắc mỗi Context chỉ nên chịu trách nhiệm 1 mảng dữ liệu.

---

## 3. React Router

Em dùng React Router v6. Sơ đồ route của em có 2 tầng bảo vệ khác nhau — đây là phần em tách riêng thành 2 component để phân biệt rõ 2 loại kiểm tra.

**File `src/routes/RequireAuth.jsx`** — tầng 1, chỉ hỏi "đã đăng nhập chưa":
```jsx
export default function RequireAuth() {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) return <LoadingSpinner />;
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;

  return <Outlet />;
}
```
Em giải thích từng nhánh: nếu `isLoading` còn `true` (đang chờ `AuthContext` gọi xong `/auth/me`), em **chưa vội quyết định** — hiển thị loading, tránh trường hợp user thực ra đã đăng nhập nhưng bị đá nhầm về `/login` chỉ vì Context chưa kịp xác nhận. Nếu chắc chắn chưa đăng nhập, dùng `<Navigate>` — đây là cách điều hướng **khai báo** (declarative) của React Router, khác với gọi hàm `navigate()` theo kiểu mệnh lệnh. Em lưu `state={{ from: location }}` — tức lưu lại đúng trang người dùng định vào, để sau khi đăng nhập xong quay lại đúng chỗ đó thay vì luôn về trang chủ.

**File `src/routes/RequireRole.jsx`** — tầng 2, dùng cho riêng khu vực admin:
```jsx
export default function RequireRole({ allowedRoles }) {
  const { user } = useAuth();
  const hasRole = user?.role && allowedRoles.includes(user.role);
  if (!hasRole) return <Navigate to="/403" replace />;
  return <Outlet />;
}
```
Component này giả định đã chắc chắn có `user` (vì nó luôn được lồng **bên trong** `RequireAuth`), chỉ kiểm tra thêm `user.role` có nằm trong danh sách `allowedRoles` được truyền vào không.

Cách lồng 2 tầng trong `AppRoutes.jsx`:
```jsx
<Route element={<RequireAuth />}>
  <Route path="cart" element={<CartPage />} />
  ...
  <Route element={<RequireRole allowedRoles={['ADMIN']} />}>
    <Route path="admin" element={<AdminDashboard />} />
    ...
  </Route>
</Route>
```
Em nhấn mạnh: `RequireRole` nằm **bên trong** `RequireAuth` — nghĩa là user chưa đăng nhập cố vào `/admin` sẽ bị `RequireAuth` chặn về `/login` trước, chỉ có user **đã đăng nhập nhưng sai role** mới thực sự chạm tới `RequireRole` và bị đá sang trang `/403`. Đây là 2 tình huống lỗi khác nhau về bản chất, nên em không gộp chung 1 màn hình.

**Route động:** `products/:id` trong `ProductDetail.jsx`:
```jsx
const { id } = useParams();
```
`useParams()` đọc phần `:id` thật trong URL, ví dụ vào `/products/7` thì `id` = `"7"`.

**Trang 404:** route cuối cùng khai báo `path="*"` — khớp với bất kỳ URL nào không match route nào ở trên.

---

## 4. Axios và kết nối API

**File `src/api/axiosClient.js`** — em chỉ tạo **đúng 1 instance Axios** dùng chung toàn app:

```jsx
const axiosClient = axios.create({ baseURL, headers: {...} });

axiosClient.interceptors.request.use((config) => {
  const token = tokenStorage.get();
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```
Request interceptor chạy **trước khi** bất kỳ request nào rời khỏi trình duyệt — em không cần tự tay gắn header `Authorization` ở từng lời gọi API, việc này được làm tự động 1 lần duy nhất ở đây.

```jsx
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.message || 'Đã có lỗi xảy ra';
    if (status === 401) {
      tokenStorage.clear();
      toast.error('Phiên đăng nhập đã hết hạn...');
      window.location.href = '/login';
    } else {
      toast.error(message);
    }
    return Promise.reject(error);
  }
);
```
Response interceptor bắt **mọi lỗi từ mọi API call trong toàn app tại một chỗ duy nhất**. Nếu là lỗi 401 (hết hạn token hoặc chưa đăng nhập), em xóa token và chuyển về trang login. Lỗi khác, em hiện toast với đúng message backend trả về. Nhờ vậy, các trang gọi API (`Login.jsx`, `CheckoutPage.jsx`...) không cần tự viết logic hiển thị lỗi — chỉ cần `catch` rỗng, vì lỗi đã được xử lý sẵn ở đây rồi.

Em tách file API theo domain, mỗi file chỉ chứa các hàm liên quan 1 nhóm chức năng, tất cả đều import `axiosClient` chung, không file nào tự `axios.create()` riêng:

```
api/authApi.js     → login, register, confirmEmail, getCurrentUser
api/productApi.js  → getAll, getById, create, update, remove
api/orderApi.js    → create, getMyOrders, getAllOrders, updateStatus
```

Ví dụ `productApi.js` — đủ 4 phương thức HTTP đề bài yêu cầu:
```jsx
const productApi = {
  getAll: (params) => axiosClient.get('/products/search', { params: {...} }).then(r => r.data),
  getById: (id) => axiosClient.get(`/products/${id}`).then(r => r.data),
  create: (data) => axiosClient.post('/products', data).then(r => r.data),
  update: (id, data) => axiosClient.put(`/products/${id}`, data).then(r => r.data),
  remove: (id) => axiosClient.delete(`/products/${id}`).then(r => r.data),
};
```

---

## 5. Chức năng CRUD

Em trình bày CRUD sản phẩm — vì đây là nơi thể hiện rõ nhất tư duy **bảo mật 2 lớp**.

**Lớp 1 — Frontend, chỉ để trải nghiệm gọn:**
```jsx
// Products.jsx
const isAdmin = user?.role === 'ADMIN';
{isAdmin && <Link to="/admin/products/new">Thêm sản phẩm</Link>}
```
Em nhấn mạnh: dòng này **không phải bảo mật thật** — nó chỉ ẩn nút cho user thường đỡ rối mắt.

**Lớp 2 — Backend, đây mới là chặn thật:**
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO dto) { ... }
```
`@PreAuthorize` chạy **trước khi** đoạn code trong hàm được thực thi — nếu token gửi lên thuộc user role `USER`, Spring Security chặn ngay tại đây, trả về `403 Forbidden`, code xử lý bên trong hoàn toàn không được chạy tới. Em có thể chứng minh trực tiếp bằng Postman: dù em có xóa dòng ẩn nút ở FE, nếu cố tình gọi thẳng API bằng token của user thường, vẫn nhận `403`.

Form `ProductForm.jsx` em dùng **chung cho cả tạo mới và sửa**:
```jsx
const { id } = useParams();
const isEdit = !!id;

useEffect(() => {
  if (!isEdit) return;
  productApi.getById(id).then((p) => reset(p));
}, [id, isEdit]);

const onSubmit = (data) => {
  return isEdit ? productApi.update(id, data) : productApi.create(data);
};
```
Nếu URL có `:id` (route `/admin/products/:id/edit`) thì `isEdit = true`, em gọi API lấy dữ liệu cũ đổ vào form bằng `reset(p)` của react-hook-form. Lúc submit, chỉ cần rẽ nhánh `isEdit ? update : create` — không cần viết 2 form riêng biệt.

Riêng phần ảnh sản phẩm, em xử lý bằng cách đọc file ảnh trên máy, chuyển thành chuỗi base64 lưu thẳng vào DB (thay vì lưu URL) — trong `ImageUploadInput.jsx`:
```jsx
const handleFile = (e) => {
  const file = e.target.files[0];
  const reader = new FileReader();
  reader.onload = () => onChange(reader.result); // "data:image/png;base64,AAAA..."
  reader.readAsDataURL(file);
};
```
Vì kiểu này tạo ra chuỗi rất dài, em đổi cột `imageUrl` bên entity `Product.java` từ `VARCHAR` sang `LONGTEXT`:
```java
@Lob
@Column(columnDefinition = "LONGTEXT")
private String imageUrl;
```

---

## 6. Form và Validation

Em dùng cặp thư viện **react-hook-form** (quản lý state form hiệu năng cao, không re-render toàn form mỗi lần gõ phím) và **yup** (khai báo luật hợp lệ tách biệt khỏi JSX, dễ đọc, dễ tái sử dụng). Nối 2 thư viện này bằng `@hookform/resolvers/yup`.

**File `src/utils/validate.js`** — em lấy ví dụ `registerSchema`:
```jsx
export const registerSchema = yup.object({
  username: yup.string().required('validation.required').min(3, 'validation.usernameMin'),
  email: yup.string().required('validation.required').email('validation.emailInvalid'),
  password: yup.string().required('validation.required')
    .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/, 'validation.passwordWeak'),
  confirmPassword: yup.string().required('validation.required')
    .oneOf([yup.ref('password')], 'validation.passwordMismatch'),
});
```
Em giải thích regex: `(?=.*[a-z])` yêu cầu có ít nhất 1 chữ thường, `(?=.*[A-Z])` chữ hoa, `(?=.*\d)` chữ số, `.{8,}` tối thiểu 8 ký tự. Dòng `confirmPassword` dùng `yup.ref('password')` để **tham chiếu chéo** sang giá trị field khác trong cùng form — đảm bảo 2 ô mật khẩu khớp nhau.

Cách dùng trong `Register.jsx`:
```jsx
const { register, handleSubmit, formState: { errors } } = useForm({
  resolver: yupResolver(registerSchema),
});
...
<input {...register('username')} />
{errors.username && <p>{t(errors.username.message)}</p>}
```
Điểm em muốn nhấn mạnh: message lỗi em viết dưới dạng **key i18n** (`'validation.required'`) chứ không phải chuỗi tiếng Việt cứng — nhờ vậy khi gọi `t(errors.username.message)`, thông báo lỗi tự động đổi ngôn ngữ theo lựa chọn hiện tại, không cần viết 2 bộ schema riêng cho tiếng Việt và tiếng Anh.

---

## 7. Vòng đời component (useEffect)

Em có 1 mẫu code lặp lại xuyên suốt dự án, lấy ví dụ ở `ProductDetail.jsx`:

```jsx
useEffect(() => {
  const controller = new AbortController();
  setIsLoading(true);

  productApi.getById(id)
    .then(setProduct)
    .catch(() => setNotFound(true))
    .finally(() => setIsLoading(false));

  return () => controller.abort();
}, [id]);
```

Em giải thích 3 điểm:
- **Dependency array `[id]`**: effect chỉ chạy lại khi `id` (lấy từ URL qua `useParams`) thay đổi. Nếu người dùng chuyển từ xem sản phẩm 5 sang sản phẩm 8, `id` đổi, effect tự chạy lại gọi API mới — nhưng nếu component re-render vì lý do khác (ví dụ đổi ngôn ngữ), effect này không chạy lại vô ích.
- **Cleanup function** (`return () => controller.abort()`): nếu người dùng bấm chuyển trang thật nhanh trước khi API kịp trả lời, request cũ bị hủy — tránh tình huống gọi `setProduct` trên 1 component đã unmount (React sẽ cảnh báo lỗi nếu việc này xảy ra, gọi là memory leak).
- Ở `ConfirmEmail.jsx`, em còn dùng thêm `useRef` để chặn effect chạy 2 lần trong môi trường dev (do React 18 StrictMode cố tình gọi effect 2 lần để phát hiện side-effect không an toàn), tránh gọi API xác nhận email 2 lần liên tiếp:
```jsx
const requested = useRef(false);
useEffect(() => {
  if (requested.current) return;
  requested.current = true;
  authApi.confirmEmail(token)...
}, [token]);
```

---

## 8. Đăng nhập và bảo vệ Route

Em trình bày trọn vẹn luồng, từ lúc bấm nút đăng nhập tới lúc mọi request sau đó tự động "biết" ai đang gọi.

```
1. Login.jsx: user nhập username/password → gọi useAuth().login(data)
2. AuthContext.login() → authApi.login(data) → POST /auth/login
3. [BE] AuthController.login():
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(...))
      → Spring Security tự gọi CustomUserDetailsService.loadUserByUsername()
        → userRepository.findByUsername() → so khớp password bằng
          passwordEncoder.matches(rawPassword, hashTrongDB) (BCrypt)
      → nếu đúng: jwtUtil.generateToken(username) → tạo chuỗi JWT
      → trả về { token, user }
4. [FE] AuthContext: tokenStorage.set(token), setUser(user)
5. Từ giờ, MỌI request qua axiosClient tự động có header
   Authorization: Bearer <token>
6. [BE] JwtAuthenticationFilter chặn MỌI request TRƯỚC KHI vào Controller:
      đọc header → jwtUtil.extractUsername() → verify hợp lệ
      → set SecurityContextHolder → request này giờ "có danh tính"
```

Em giải thích vì sao chọn JWT thay vì session truyền thống: session là **stateful** — server phải tự nhớ ai đang đăng nhập (lưu trong bộ nhớ hoặc DB). JWT là **stateless** — mọi thông tin cần thiết (username, thời hạn) nằm ngay trong chuỗi token, server chỉ cần verify chữ ký số bằng secret key, không cần tra cứu gì thêm. Đây là lý do JWT phù hợp hơn với kiến trúc REST API tách rời frontend/backend.

**Đăng xuất** — vì JWT là stateless, em không cần gọi API logout để "hủy" token phía server:
```jsx
const logout = () => { tokenStorage.clear(); setUser(null); };
```
Chỉ cần xóa token khỏi localStorage, phía client không còn gửi token đi nữa — coi như đã đăng xuất, token cũ vẫn "hợp lệ về kỹ thuật" cho tới khi tự hết hạn nhưng không còn được dùng.

---

## 9. Tìm kiếm, lọc và phân trang

Em thiết kế toàn bộ state lọc/trang **sống trên URL**, không dùng `useState` nội bộ — dùng hook `useSearchParams` của React Router.

```jsx
// Products.jsx
const [searchParams, setSearchParams] = useSearchParams();
const page = Number(searchParams.get('page') ?? 0);
const name = searchParams.get('name') ?? undefined;
```
Người dùng gõ vào form lọc, bấm nút → `ProductFilter` gọi callback `onApply(params)` → `Products.jsx` gọi `setSearchParams(params)` — **đổi hẳn URL**, ví dụ thành `/products?name=áo&page=0`. Vì `searchParams` đổi, React tự re-render component với giá trị mới, và:
```jsx
useEffect(() => {
  productApi.getAll({ name, minPrice, maxPrice, page, size: PAGE_SIZE }).then(...);
}, [name, minPrice, maxPrice, page]);
```
effect tự chạy lại gọi API đúng bộ lọc mới. Em giải thích lý do chọn cách này: URL trở thành **nguồn sự thật duy nhất** — người dùng có thể copy link gửi bạn bè để chia sẻ đúng kết quả đang lọc, bấm nút Back/Forward của trình duyệt hoạt động đúng trực giác, và F5 reload trang không làm mất bộ lọc đang chọn.

Phía backend, em dùng **JPA Specification** để build câu `WHERE` động — `ProductSpecification.java`:
```java
public static Specification<Product> minPrice(BigDecimal min) {
  return (root, query, builder) -> min == null
      ? builder.conjunction()
      : builder.greaterThanOrEqualTo(root.get("price"), min);
}
```
`builder.conjunction()` tức là "điều kiện luôn đúng" — nếu người dùng không nhập giá tối thiểu, điều kiện này coi như không tồn tại, không ảnh hưởng tới kết quả. Các specification được kết hợp bằng `.and()` trong `ProductService.searchProducts()`, cho phép linh hoạt bất kỳ tổ hợp filter nào (chỉ tên, chỉ giá, cả hai, hoặc không gì cả) mà không cần viết nhiều query riêng lẻ cho từng trường hợp.

Về phân trang, em dùng `Pageable`/`Page` có sẵn của Spring Data, kèm chỉ định sắp xếp cố định để đảm bảo thứ tự ổn định giữa các trang:
```java
@GetMapping("/search")
public ResponseEntity<Page<ProductDTO>> searchProducts(
    @RequestParam(required = false) String q, ...,
    @PageableDefault(size = 3, sort = "id") Pageable pageable) { ... }
```

---

## 10. Đa ngôn ngữ (i18n)

Em dùng thư viện `react-i18next`, 2 file JSON chứa toàn bộ chuỗi text: `i18n/locales/vi.json` và `en.json`, cùng cấu trúc key, khác nội dung dịch.

```jsx
// i18n/index.js
i18n.use(LanguageDetector).use(initReactI18next).init({
  resources: { vi: { translation: vi }, en: { translation: en } },
  lng: langStorage.get() || 'vi',
  fallbackLng: 'vi',
});
```
Mọi component gọi `const { t } = useTranslation()`, hiển thị `t('nav.products')` thay vì hard-code chữ "Sản phẩm" trực tiếp trong JSX.

`LanguageSwitcher.jsx`:
```jsx
<button onClick={() => changeLanguage('en')}>EN</button>
```
```jsx
// i18n/index.js
export function changeLanguage(lang) {
  i18n.changeLanguage(lang);
  langStorage.set(lang);
}
```
`i18n.changeLanguage('en')` khiến **toàn bộ app re-render** với bản dịch tiếng Anh — mọi component đang gọi `useTranslation()` đều tự động cập nhật, em không cần tự viết logic re-render tay. Dòng `langStorage.set(lang)` ghi lựa chọn vào localStorage, để `i18n/index.js` đọc lại đúng ngôn ngữ này ở lần mở trang tiếp theo.

---

## 11. Mã nguồn

Em tổ chức thư mục theo nguyên tắc **phân tầng rõ trách nhiệm**:

```
api/         → tầng DUY NHẤT gọi axios trực tiếp
contexts/    → global state
hooks/       → custom hook bọc quanh Context
pages/       → 1 file = 1 trang, DUY NHẤT nơi gọi useEffect + api/*
components/  → UI nhỏ, tái sử dụng, KHÔNG tự gọi API — nhận data qua props
routes/      → cấu hình định tuyến
utils/       → hàm thuần (validate, format, storage)
i18n/        → đa ngôn ngữ
```

Em nhấn mạnh nguyên tắc quan trọng nhất: `components/` **không bao giờ** tự gọi API — chỉ `pages/` mới làm việc đó, rồi truyền dữ liệu xuống `components/` qua props. Nhờ vậy, ví dụ `ProductCard` có thể tái sử dụng ở cả `HomePage` lẫn `Products` mà không cần biết trang nào đang dùng nó, không phụ thuộc logic gọi API nào.

---

## Kết thúc phần trình bày — sẵn sàng trả lời câu hỏi

Em đã trình bày xong toàn bộ 11 tiêu chí, đi từ luồng chạy tổng thể tới chi tiết từng dòng code quan trọng và cách các file liên kết với nhau. Em sẵn sàng trả lời câu hỏi của thầy/cô.
