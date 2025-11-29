-- Tao database
DROP DATABASE IF EXISTS Testing_System ;
CREATE DATABASE Testing_System ;
USE Testing_System;

-- 1. Tao bang Department
DROP TABLE IF EXISTS Department;
CREATE TABLE Department(
	DepartmentID 	TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    DepartmentName 	NVARCHAR(30) NOT NULL
);

-- 2. Tao bang Posittion
DROP TABLE IF EXISTS Position;
CREATE TABLE `Position` (
	PositionID		TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    PositionName	ENUM('Dev','Test','Scrum Master','PM') NOT NULL
);

-- 3. Tao bang Account
DROP TABLE IF EXISTS `Account`;
CREATE TABLE `Account`(
	AccountID		TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    Email			VARCHAR(50) NOT NULL UNIQUE KEY,
    Username		VARCHAR(50) NOT NULL UNIQUE KEY,
    FullName		NVARCHAR(50) NOT NULL,
    DepartmentID 	TINYINT UNSIGNED ,
    PositionID		TINYINT UNSIGNED NOT NULL,
    CreateDate		DATETIME DEFAULT NOW(),
    FOREIGN KEY(DepartmentID) REFERENCES Department(DepartmentID) ,
    FOREIGN KEY(PositionID) REFERENCES `Position`(PositionID) 
);

-- 4. Tao bang Group
DROP TABLE IF EXISTS `Group`;
CREATE TABLE `Group`(
	GroupID					TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    GroupName				NVARCHAR(50) NOT NULL UNIQUE KEY,
    CreatorID				TINYINT UNSIGNED,
    CreateDate				DATETIME DEFAULT NOW(),
    FOREIGN KEY(CreatorID) 	REFERENCES `Account`(AccountId) 
);

-- 5. Tao bang GroupAccount
DROP TABLE IF EXISTS GroupAccount;
CREATE TABLE GroupAccount(
	GroupID					TINYINT UNSIGNED NOT NULL,
    AccountID				TINYINT UNSIGNED NOT NULL,
    JoinDate				DATETIME DEFAULT NOW(),
    PRIMARY KEY (GroupID,AccountID),
    FOREIGN KEY(GroupID) REFERENCES `Group`(GroupID) ,
    FOREIGN KEY(AccountID) REFERENCES `Account`(AccountID)
);

-- 6. Tao bang TypeQuestion
DROP TABLE IF EXISTS TypeQuestion;
CREATE TABLE TypeQuestion (
    TypeID 			TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    TypeName 		ENUM('Essay','Multiple-Choice') NOT NULL UNIQUE KEY
);

-- 7. Tao bang CategoryQuestion
DROP TABLE IF EXISTS CategoryQuestion;
CREATE TABLE CategoryQuestion(
    CategoryID				TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    CategoryName			NVARCHAR(50) NOT NULL UNIQUE KEY
);

-- 8. Tao bang Question
DROP TABLE IF EXISTS Question;
CREATE TABLE Question(
    QuestionID				TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    Content					NVARCHAR(100) NOT NULL,
    CategoryID				TINYINT UNSIGNED NOT NULL,
    TypeID					TINYINT UNSIGNED NOT NULL,
    CreatorID				TINYINT UNSIGNED NOT NULL,
    CreateDate				DATETIME DEFAULT NOW(),
    FOREIGN KEY(CategoryID) 	REFERENCES CategoryQuestion(CategoryID) ,
    FOREIGN KEY(TypeID) 		REFERENCES TypeQuestion(TypeID),
    FOREIGN KEY(CreatorID) 		REFERENCES `Account`(AccountId)  
);

-- 9. Tao bang Answer
DROP TABLE IF EXISTS Answer;
CREATE TABLE Answer(
    AnswerID				TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    Content					NVARCHAR(100) NOT NULL,
    QuestionID				TINYINT UNSIGNED NOT NULL,
    isCorrect				BIT DEFAULT 1,
    FOREIGN KEY(QuestionID) REFERENCES Question(QuestionID) 
);

-- 10. Tao bang Exam
DROP TABLE IF EXISTS Exam;
CREATE TABLE Exam(
    ExamID					TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `Code`					CHAR(10) NOT NULL,
    Title					NVARCHAR(50) NOT NULL,
    CategoryID				TINYINT UNSIGNED NOT NULL,
    Duration				TINYINT UNSIGNED NOT NULL,
    CreatorID				TINYINT UNSIGNED NOT NULL,
    CreateDate				DATETIME DEFAULT NOW(),
    FOREIGN KEY(CategoryID) REFERENCES CategoryQuestion(CategoryID),
    FOREIGN KEY(CreatorID) 	REFERENCES `Account`(AccountId) 
);

-- 11. Tao bang ExamQuestion
DROP TABLE IF EXISTS ExamQuestion;
CREATE TABLE ExamQuestion(
    ExamID				TINYINT UNSIGNED NOT NULL,
	QuestionID			TINYINT UNSIGNED NOT NULL,
    FOREIGN KEY(QuestionID) REFERENCES Question(QuestionID) ,
    FOREIGN KEY(ExamID) REFERENCES Exam(ExamID) ,
    PRIMARY KEY (ExamID,QuestionID)
);
-- Them data vao bang Department
INSERT INTO Department(DepartmentName) 
VALUES
						('Marketing'	),
						('Sale'		), 	
						(N'Bảo vệ'		),
						(N'Nhân sự'		),
						(N'Kỹ thuật'	),
						(N'Tài chính'	),
						(N'Phó giám đốc'),
						(N'Giám đốc'	),
						(N'Thư kí'		),
						(N'Bán hàng'	);
    
-- Them data vao bang Position
INSERT INTO Position	(PositionName	) 
VALUES 					('Dev'			),
						('Test'			),
						('Scrum Master'	),
						('PM'			); 
INSERT INTO `Account`(Email								, Username			, FullName				, DepartmentID	, PositionID, CreateDate)
VALUES 				('trungtran1@gmail.com'				, 'Username1'		,'Fullname1'				,   '5'			,   '1'		,'2025-11-18'),
					('trungtran12@gmail.com'				, 'Username2'		,'Fullname2'				,   '1'			,   '2'		,'2025-11-18'),
                    ('Email3@gmail.com'				, 'Username3'		,'Fullname3'				,   '2'			,   '2'		,'2025-11-18'),
                    ('Email4@gmail.com'				, 'Username4'		,'Fullname4'				,   '3'			,   '4'		,'2025-11-18'),
                    ('Email5@gmail.com'				, 'Username5'		,'Fullname5'				,   '4'			,   '4'		,'2025-11-18'),
                    ('Email6@gmail.com'				, 'Username6'		,'Fullname6'				,   '6'			,   '3'		,'2025-11-18'),
                    ('Email7@gmail.com'				, 'Username7'		,'Fullname7'				,   '2'			,   '2'		, NULL		),
                    ('Email8@gmail.com'				, 'Username8'		,'Fullname8'				,   '8'			,   '1'		,'2025-11-18'),
                    ('Email9@gmail.com'				, 'Username9'		,'Fullname9'				,   '2'			,   '2'		,'2025-11-18'),
                    ('Email10@gmail.com'			, 'Username10'		,'Fullname10'				,   '10'		,   '1'		,'2025-11-18');

-- Them data vao bang Group
INSERT INTO `Group`	(  GroupName			, CreatorID		, CreateDate)
VALUES 				(N'Testing System'		,   5			,'2025-11-18'),
					(N'Develop'			,   1			,'2025-11-18'),
                    (N'Sale 01'			,   2			,'2025-11-18'),
                    (N'Sale 02'			,   3			,'2025-11-18'),
                    (N'Sale 03'			,   4			,'2025-11-18'),
                    (N'Content Creator'			,   6			,'2025-11-18'),
                    (N'Marketing 01'	,   7			,'2025-11-18'),
                    (N'Management'			,   8			,'2025-11-18'),
                    (N'World'		,   9			,'2025-11-18'),
                    (N'Trunggggggggg'			,   10			,'2025-11-18');

-- Them data vao bang GroupAccount
INSERT INTO `GroupAccount`	(  GroupID	, AccountID	, JoinDate	 )
VALUES 						(	1		,    1		,'2025-11-18'),
							(	1		,    2		,'2025-11-18'),
							(	3		,    3		,'2025-11-18'),
							(	3		,    4		,'2025-11-18'),
							(	5		,    5		,'2025-11-18'),
							(	1		,    3		,'2025-11-18'),
							(	1		,    7		,'2025-11-18'),
							(	8		,    3		,'2025-11-18'),
							(	1		,    9		,'2025-11-18'),
							(	10		,    10		,'2025-11-18');


-- Them data vao bang TypeQuestion
INSERT INTO TypeQuestion	(TypeName			) 
VALUES 						('Essay'			), 
							('Multiple-Choice'	); 


-- Add data CategoryQuestion
INSERT INTO CategoryQuestion		(CategoryName	)
VALUES 								('Java'			),
									('NET'		),
									('HTML'		),
									('SQL'			),
									('Postman'		),
									('Ruby'			),
									('Python'		),
									('C++'			),
									('C#'		),
									('PHP'			);
													
-- Them data vao bang Question
INSERT INTO Question	(Content			, CategoryID, TypeID		, CreatorID	, CreateDate )
VALUES 					(N'Câu hỏi về Java'	,	1		,   '1'			,   '2'		,'2025-11-18'),
						(N'Câu Hỏi về PHP'	,	10		,   '2'			,   '2'		,'2025-11-18'),
						(N'Hỏi về C#'		,	9		,   '2'			,   '3'		,'2025-11-18'),
						(N'Hỏi về Ruby'		,	6		,   '1'			,   '4'		,'2025-11-18'),
						(N'Hỏi về Postman'	,	5		,   '1'			,   '5'		,'2025-11-18'),
						(N'Hỏi về ADO.NET'	,	3		,   '2'			,   '6'		,'2025-11-18'),
						(N'Hỏi về ASP.NET'	,	2		,   '1'			,   '7'		,'2025-11-18'),
						(N'Hỏi về C++'		,	8		,   '1'			,   '8'		,'2025-11-18'),
						(N'Hỏi về SQL'		,	4		,   '2'			,   '9'		,'2025-11-18'),
						(N'Hỏi về Python'	,	7		,   '1'			,   '10'	,'2025-11-18');

-- Them data vao bang Answers
INSERT INTO Answer	(  Content		, QuestionID	, isCorrect	)
VALUES 				(N'Trả lời 01'	,   1			,	0		),
					(N'Trả lời 02'	,   1			,	1		),
                    (N'Trả lời 03'	,   1			,	0		),
                    (N'Trả lời 04'	,   1			,	1		),
                    (N'Trả lời 05'	,   2			,	1		),
                    (N'Trả lời 06'	,   3			,	1		),
                    (N'Trả lời 07'	,   4			,	0		),
                    (N'Trả lời 08'	,   8			,	0		),
                    (N'Trả lời 09'	,   9			,	1		),
                    (N'Trả lời 10'	,   10			,	1		);
	
-- Them data vao bang Exam
INSERT INTO Exam	(`Code`			, Title					, CategoryID	, Duration	, CreatorID		, CreateDate )
VALUES 				('01'		, N'Đề thi C#'			,	1			,	60		,   '5'			,'2025-11-18'),
					('02'		, N'Đề thi PHP'			,	10			,	60		,   '2'			,'2025-11-18'),
                    ('03'		, N'Đề thi C++'			,	9			,	120		,   '2'			,'2025-11-18'),
                    ('04'		, N'Đề thi Java'		,	6			,	60		,   '3'			,'2025-11-18'),
                    ('05'		, N'Đề thi Ruby'		,	5			,	120		,   '4'			,'2025-11-18'),
                    ('06'		, N'Đề thi Postman'		,	3			,	60		,   '6'			,'2025-11-18'),
                    ('07'		, N'Đề thi SQL'			,	2			,	60		,   '7'			,'2025-11-18'),
                    ('08'		, N'Đề thi Python'		,	8			,	60		,   '8'			,'2025-11-18'),
                    ('09'		, N'Đề thi CSS'		,	4			,	90		,   '9'			,'2025-11-18'),
                    ('10'		, N'Đề thi HTML'		,	7			,	90		,   '10'		,'2025-11-18');
                    
                    
-- Them data vao bang ExamQuestion
INSERT INTO ExamQuestion(ExamID, QuestionID) 
VALUES 
    (1, 5), 
    (1, 10), 
    (1, 4), 
    (1, 3), 
    (1, 7), 
    (1, 1), 
    (1, 2), 
    (1, 6), 
    (1, 9), 
    (1, 8);
    
    

-- question 1 : tao view co chua danh sach nhan vien thuoc phong ban sale 
CREATE OR REPLACE VIEW vw AS 
	SELECT a.AccountID ,a.Username ,a.FullName ,a.DepartmentID , d.DepartmentName FROM account a
    INNER JOIN department d ON a.departmentID = d.departmentID 
    Where departmentName = 'Sale';
    
SELECT * FROM vw ;
-- question 2 : Tạo view có chứa thông tin các account tham gia vào nhiều group nhất

CREATE OR REPLACE VIEW vw_groupAccount AS
	WITH cte_maxAccount AS(
		SELECT count(*) AS Amount FROM groupaccount ga
		GROUP BY ga.AccountID
	)
	SELECT ga.AccountID, a.FullName, COUNT(*) Amount FROM groupaccount ga
	INNER JOIN account a ON a.AccountID = ga.AccountID
	GROUP BY ga.AccountID
	HAVING COUNT(*) = (SELECT max(Amount) FROM cte_maxAccount);

-- Question 3 : Tạo view có chứa câu hỏi có những content quá dài (content quá 300 từ được coi là quá dài) và xóa nó đi
CREATE OR REPLACE VIEW vw_Contentquadai AS
	SELECT *
	FROM Question
	WHERE LENGTH(Content) > 300 ;
	
SELECT * FROM vw_Contentquadai;
DROP VIEW vw_Contentquadai;







