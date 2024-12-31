# - 회원 테이블 생성

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_name VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(10) NOT NULL
);
#  - 상품 테이블 생성
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          product_name VARCHAR(255) NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          product_price DECIMAL(10, 2) NOT NULL,
                          product_open_date DATETIME NOT NULL,
                          quantity INTEGER NOT NULL,
                          status VARCHAR(255) NOT NULL,
                          CONSTRAINT chk_status CHECK (status IN ('Pre-sale', 'On Sale', 'Sold Out'))
);
# 장바구니 테이블 생성
CREATE TABLE carts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users(ID) ON DELETE CASCADE
);
# - 장바구니 상품 테이블 생성
CREATE TABLE cart_products (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               cart_id BIGINT NOT NULL,
                               product_id BIGINT NOT NULL,
                               quantity INT NOT NULL,
                               FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
                               FOREIGN KEY (product_id) REFERENCES products(id)
);

# - 주문 테이블 생성
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        created_At DATETIME NOT NULL,
                        updated_At DATETIME NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id)
);
# - 주문 상품 테이블 생성
CREATE TABLE order_products (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                order_id BIGINT NOT NULL,
                                product_id BIGINT NOT NULL,
                                quantity INTEGER NOT NULL,
                                price DECIMAL(10, 2) NOT NULL,
                                FOREIGN KEY (order_id) REFERENCES orders(id),
                                FOREIGN KEY (product_id) REFERENCES products(id)
);
# 상품의 정보를 변경시키기위해 복합 인덱스 설정
CREATE INDEX idx_status_open_date ON products (status, product_open_date);

# 상품더미
INSERT INTO products (product_name, description, quantity, product_open_date, product_price, status) VALUES
                                                                                                         ('지민 한정판 후드티', 'BTS 지민의 한정판 후드티로 독점 디자인이 특징입니다.', 100, '2024-12-01 10:00:00', 120.00, 'Pre-sale'),
                                                                                                         ('아이언맨 액션피규어', '마블 시리즈의 한정판 아이언맨 액션 피규어입니다.', 50, '2024-12-02 11:00:00', 150.00, 'On Sale'),
                                                                                                         ('세일러문 목걸이', '세일러문 캐릭터에서 영감을 얻은 아름다운 목걸이입니다.', 200, '2024-12-03 12:00:00', 90.00, 'Sold Out'),
                                                                                                         ('나루토 질풍전 포스터', '나루토 질풍전 시리즈의 고급 포스터입니다.', 150, '2024-12-04 13:00:00', 40.00, 'Pre-sale'),
                                                                                                         ('슈퍼 사이얀 손오공 액션피규어', '슈퍼 사이얀 형태의 손오공 액션 피규어 한정판입니다.', 80, '2024-12-05 14:00:00', 180.00, 'Sold Out'),
                                                                                                         ('블랙핑크 한정판 앨범', '블랙핑크의 한정판 앨범과 독점 포토카드 세트입니다.', 250, '2024-12-06 15:00:00', 250.00, 'On Sale'),
                                                                                                         ('미키마우스 한정판 인형', '미키마우스 팬들을 위한 한정판 인형입니다.', 300, '2024-12-07 16:00:00', 45.00, 'Pre-sale'),
                                                                                                         ('해리포터 지팡이 복제본', '해리포터의 지팡이를 정밀하게 복제한 고퀄리티 제품입니다.', 120, '2024-12-08 17:00:00', 120.00, 'On Sale'),
                                                                                                         ('스파이더맨 후드티', '스파이더맨을 테마로 한 후드티입니다.', 130, '2024-12-09 18:00:00', 110.00, 'Sold Out'),
                                                                                                         ('BTS 포토프레임 세트', 'BTS 멤버들의 포토를 담을 수 있는 포토프레임 세트입니다.', 150, '2024-12-10 19:00:00', 75.00, 'Pre-sale'),
                                                                                                         ('피카츄 라이트', '피카츄 모양의 조명이 켜지는 팬용 아이템입니다.', 200, '2024-12-11 20:00:00', 65.00, 'On Sale'),
                                                                                                         ('원더우먼 액션피규어', '움직일 수 있는 부품을 갖춘 원더우먼 한정판 액션피규어입니다.', 60, '2024-12-12 21:00:00', 135.00, 'Sold Out'),
                                                                                                         ('루피 스트로우햇 복제본', '원피스의 루피가 착용한 스트로우햇 복제본입니다.', 180, '2024-12-13 22:00:00', 80.00, 'Pre-sale'),
                                                                                                         ('조조의 기묘한 모험 아트북', '조조의 기묘한 모험 팬들을 위한 독점 아트북입니다.', 90, '2024-12-14 23:00:00', 70.00, 'On Sale'),
                                                                                                         ('배트맨 한정판 티셔츠', '배트맨 로고 디자인의 한정판 티셔츠입니다.', 250, '2024-12-15 10:00:00', 60.00, 'Sold Out'),
                                                                                                         ('엘사 겨울왕국 인형', '겨울왕국의 엘사 인형으로 한정판 의상이 특징입니다.', 220, '2024-12-16 11:00:00', 50.00, 'Pre-sale'),
                                                                                                         ('볼드모트 지팡이 복제본', '볼드모트의 지팡이를 정밀하게 복제한 고급 제품입니다.', 100, '2024-12-17 12:00:00', 110.00, 'On Sale'),
                                                                                                         ('더 만달로리안 헬멧', '더 만달로리안 시리즈의 고품질 헬멧 복제본입니다.', 50, '2024-12-18 13:00:00', 250.00, 'Sold Out'),
                                                                                                         ('원더우먼 한정판 포스터', '원더우먼의 독점 디자인 포스터입니다.', 120, '2024-12-19 14:00:00', 30.00, 'Pre-sale'),
                                                                                                         ('드래곤볼 Z 머그컵', '드래곤볼 Z의 손오공 머그컵 한정판입니다.', 300, '2024-12-20 15:00:00', 25.00, 'On Sale'),
                                                                                                         ('스폰지밥 백팩', '스폰지밥 팬들을 위한 한정판 백팩입니다.', 180, '2024-12-21 16:00:00', 70.00, 'Pre-sale'),
                                                                                                         ('로키 티셔츠', '마블의 로키 캐릭터 디자인 티셔츠입니다.', 150, '2024-12-22 17:00:00', 65.00, 'On Sale'),
                                                                                                         ('스타워즈 라이트세이버', '스타워즈의 라이트세이버 복제본으로 빛과 소리 효과가 있습니다.', 40, '2024-12-23 18:00:00', 200.00, 'Sold Out'),
                                                                                                         ('토토로 인형', '스튜디오 지브리의 토토로 인형입니다.', 250, '2024-12-24 19:00:00', 55.00, 'Pre-sale'),
                                                                                                         ('BTS 팬 라이트스틱', 'BTS 팬들을 위한 한정판 라이트스틱입니다.', 100, '2024-12-25 20:00:00', 120.00, 'On Sale'),
                                                                                                         ('나루토 닌자 머리띠', '나루토 팬을 위한 닌자 머리띠입니다.', 220, '2024-12-26 21:00:00', 45.00, 'Sold Out'),
                                                                                                         ('캡틴 아메리카 방패 복제본', '캡틴 아메리카의 방패 복제본으로 한정판 제품입니다.', 60, '2024-12-27 22:00:00', 180.00, 'Pre-sale'),
                                                                                                         ('데스노트 한정판 세트', '데스노트 팬들을 위한 한정판 세트입니다.', 80, '2024-12-28 23:00:00', 160.00, 'On Sale'),
                                                                                                         ('세일러 마스 팔찌', '세일러문 팬들을 위한 세일러 마스 팔찌입니다.', 200, '2024-12-29 10:00:00', 50.00, 'Sold Out'),
                                                                                                         ('더 위쳐 게임 박스', '더 위쳐 게임의 특별 한정판 박스 세트입니다.', 70, '2024-12-30 11:00:00', 300.00, 'Pre-sale');

INSERT INTO users (user_name, phone_number, address, email, password, role) VALUES
                                                                                ('김민지', '010-1234-5678', '서울시 강남구 역삼동', 'minji.kim@email.com', 'password123', 'USER'),
                                                                                ('이철수', '010-2345-6789', '서울시 서초구 방배동', 'chulsoo.lee@email.com', 'password456', 'USER'),
                                                                                ('박지현', '010-3456-7890', '서울시 송파구 잠실동', 'jihyun.park@email.com', 'password789', 'USER'),
                                                                                ('정상호', '010-4567-8901', '서울시 마포구 합정동', 'sangho.jung@email.com', 'password101', 'USER'),
                                                                                ('홍길동', '010-5678-9012', '서울시 용산구 이태원동', 'gildong.hong@email.com', 'password202', 'USER'),
                                                                                ('김수연', '010-6789-0123', '서울시 종로구 종로동', 'sooyeon.kim@email.com', 'password303', 'USER'),
                                                                                ('이수정', '010-7890-1234', '서울시 강북구 수유동', 'sujeong.lee@email.com', 'password404', 'USER'),
                                                                                ('최영수', '010-8901-2345', '서울시 구로구 고척동', 'youngsoo.choi@email.com', 'password505', 'USER'),
                                                                                ('장민아', '010-9012-3456', '서울시 동대문구 휘경동', 'mina.jang@email.com', 'password606', 'USER'),
                                                                                ('김영수', '010-0123-4567', '서울시 은평구 신사동', 'youngsoo.kim@email.com', 'password707', 'USER');

-- carts 테이블에 더미 데이터 추가 (장바구니 번호 1부터 10까지)
INSERT INTO carts (user_id) VALUES
                                (1),
                                (2),
                                (3),
                                (4),
                                (5),
                                (6),
                                (7),
                                (8),
                                (9),
                                (10);
-- 1번 장바구니에 2개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (1, 1, 2),
                                                              (1, 2, 3);

-- 2번 장바구니에 3개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (2, 3, 1),
                                                              (2, 4, 2),
                                                              (2, 5, 5);

-- 3번 장바구니에 2개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (3, 6, 1),
                                                              (3, 7, 4);

-- 4번 장바구니에 3개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (4, 8, 3),
                                                              (4, 9, 2),
                                                              (4, 10, 1);

-- 5번 장바구니에 2개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (5, 11, 1),
                                                              (5, 12, 2);

-- 6번 장바구니에 3개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (6, 13, 2),
                                                              (6, 14, 1),
                                                              (6, 15, 3);

-- 7번 장바구니에 2개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (7, 16, 1),
                                                              (7, 17, 2);

-- 8번 장바구니에 3개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (8, 18, 5),
                                                              (8, 19, 3),
                                                              (8, 20, 2);

-- 9번 장바구니에 2개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (9, 21, 1),
                                                              (9, 22, 2);

-- 10번 장바구니에 3개의 상품 추가
INSERT INTO cart_products (cart_id, product_id, quantity) VALUES
                                                              (10, 23, 4),
                                                              (10, 24, 2),
                                                              (10, 25, 1);