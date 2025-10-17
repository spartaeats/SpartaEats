-- ============================================
-- SpartaEats 시드 데이터 (PostgreSQL)
-- ============================================

-- ============================================
-- 1. 사용자 데이터 (p_user)
-- 비밀번호는 모두 "password123" (BCrypt 암호화)
-- ============================================
INSERT INTO p_user (user_id, nickname, email, phone, password, role, is_public, created_at, updated_at, created_by, updated_by, last_login_at) VALUES
('admin', '관리자', 'admin@spartaeats.com', '01012345678', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'MASTER', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('owner001', '김사장', 'owner001@spartaeats.com', '01023456789', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'OWNER', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('owner002', '이사장', 'owner002@spartaeats.com', '01034567890', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'OWNER', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('owner003', '박사장', 'owner003@spartaeats.com', '01045678901', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'OWNER', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('customer001', '홍길동', 'customer001@test.com', '01056789012', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'CUSTOMER', false, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('customer002', '김철수', 'customer002@test.com', '01067890123', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'CUSTOMER', false, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('customer003', '이영희', 'customer003@test.com', '01078901234', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'CUSTOMER', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW()),
('manager001', '매니저', 'manager@spartaeats.com', '01089012345', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13AavhdRWsePYR5i', 'MANAGER', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM', NOW());

-- ============================================
-- 2. 가게 카테고리 (p_category)
-- ============================================
INSERT INTO p_category (id, cate_01, name) VALUES
(uuid_generate_v4(), 'KOR', '한식'),
(uuid_generate_v4(), 'CHN', '중식'),
(uuid_generate_v4(), 'JPN', '일식'),
(uuid_generate_v4(), 'WST', '양식'),
(uuid_generate_v4(), 'CHK', '치킨'),
(uuid_generate_v4(), 'PIZ', '피자'),
(uuid_generate_v4(), 'BRG', '버거'),
(uuid_generate_v4(), 'CAF', '카페/디저트'),
(uuid_generate_v4(), 'ASN', '아시안'),
(uuid_generate_v4(), 'ETC', '기타');

-- ============================================
-- 3. 가게 정보 (p_store)
-- ============================================
INSERT INTO p_store (id, name, address, addr_detail, image, phone, open_hour, close_hour, status_day, description, status, add_lat, add_lng, category_id, user_id) VALUES
(uuid_generate_v4(), '맛있는 한식당', '서울특별시 강남구 테헤란로 123', '1층', 'https://example.com/images/korean.jpg', '0212345678', '09:00:00', '22:00:00', '월~일', '전통 한식을 현대적으로 재해석한 맛집', true, 37.4979502, 127.0276368, (SELECT id FROM p_category WHERE cate_01 = 'KOR' LIMIT 1), 'owner001'),
(uuid_generate_v4(), '중화루', '서울특별시 송파구 올림픽로 456', '2층', 'https://example.com/images/chinese.jpg', '0223456789', '11:00:00', '23:00:00', '월~일', '정통 중화요리 전문점', true, 37.5141111, 127.1060000, (SELECT id FROM p_category WHERE cate_01 = 'CHN' LIMIT 1), 'owner001'),
(uuid_generate_v4(), '스시마스터', '서울특별시 서초구 강남대로 789', '지하1층', 'https://example.com/images/sushi.jpg', '0234567890', '12:00:00', '22:00:00', '화~일', '신선한 재료로 만드는 프리미엄 스시', true, 37.4925361, 127.0307278, (SELECT id FROM p_category WHERE cate_01 = 'JPN' LIMIT 1), 'owner002'),
(uuid_generate_v4(), '파스타하우스', '서울특별시 마포구 월드컵북로 101', '1층', 'https://example.com/images/pasta.jpg', '0245678901', '11:30:00', '21:30:00', '월~토', '수제 파스타와 피자를 즐길 수 있는 이탈리안 레스토랑', true, 37.5581613, 126.9375783, (SELECT id FROM p_category WHERE cate_01 = 'WST' LIMIT 1), 'owner002'),
(uuid_generate_v4(), '굽네치킨 강남점', '서울특별시 강남구 역삼로 222', '1층', 'https://example.com/images/chicken.jpg', '0256789012', '11:00:00', '23:59:59', '월~일', '바삭하고 맛있는 치킨 전문점', true, 37.4963254, 127.0304524, (SELECT id FROM p_category WHERE cate_01 = 'CHK' LIMIT 1), 'owner003'),
(uuid_generate_v4(), '피자알볼로', '서울특별시 용산구 이태원로 333', '1층', 'https://example.com/images/pizza.jpg', '0267890123', '10:00:00', '23:00:00', '월~일', '나폴리 정통 화덕 피자', true, 37.5347267, 126.9945733, (SELECT id FROM p_category WHERE cate_01 = 'PIZ' LIMIT 1), 'owner003'),
(uuid_generate_v4(), '쉐이크쉑 명동점', '서울특별시 중구 명동길 444', '1층', 'https://example.com/images/burger.jpg', '0278901234', '10:30:00', '22:00:00', '월~일', '프리미엄 수제버거', true, 37.5631642, 126.9861844, (SELECT id FROM p_category WHERE cate_01 = 'BRG' LIMIT 1), 'owner001'),
(uuid_generate_v4(), '스타벅스 역삼점', '서울특별시 강남구 테헤란로 555', '1층', 'https://example.com/images/cafe.jpg', '0289012345', '07:00:00', '22:00:00', '월~일', '커피와 디저트를 즐기는 카페', true, 37.5009545, 127.0358380, (SELECT id FROM p_category WHERE cate_01 = 'CAF' LIMIT 1), 'owner002'),
(uuid_generate_v4(), '타이푸드', '서울특별시 성동구 왕십리로 666', '2층', 'https://example.com/images/thai.jpg', '0290123456', '11:00:00', '21:00:00', '월~일', '정통 태국 요리', true, 37.5633967, 127.0383750, (SELECT id FROM p_category WHERE cate_01 = 'ASN' LIMIT 1), 'owner003');

-- ============================================
-- 4. 메뉴 카테고리 (p_item_category)
-- ============================================
INSERT INTO p_item_category (id, category_id, name) VALUES
(uuid_generate_v4(), 'MAIN', '메인 메뉴'),
(uuid_generate_v4(), 'SIDE', '사이드 메뉴'),
(uuid_generate_v4(), 'DRINK', '음료'),
(uuid_generate_v4(), 'DESSERT', '디저트'),
(uuid_generate_v4(), 'SET', '세트 메뉴'),
(uuid_generate_v4(), 'SPECIAL', '스페셜');

-- ============================================
-- 5. 메뉴 아이템 (p_item)
-- ============================================

-- 맛있는 한식당 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '김치찌개', '얼큰한 김치찌개', 9000, 8000, 'https://example.com/items/kimchi.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '맛있는 한식당' LIMIT 1)),
(uuid_generate_v4(), '된장찌개', '구수한 된장찌개', 8000, null, 'https://example.com/items/doenjang.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '맛있는 한식당' LIMIT 1)),
(uuid_generate_v4(), '불고기', '달콤한 양념 불고기', 15000, 13000, 'https://example.com/items/bulgogi.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '맛있는 한식당' LIMIT 1)),
(uuid_generate_v4(), '공기밥', '따끈한 쌀밥', 1000, null, 'https://example.com/items/rice.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'SIDE' LIMIT 1), (SELECT id FROM p_store WHERE name = '맛있는 한식당' LIMIT 1));

-- 중화루 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '짜장면', '정통 중화 짜장면', 7000, null, 'https://example.com/items/jajang.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '중화루' LIMIT 1)),
(uuid_generate_v4(), '짬뽕', '얼큰한 해물 짬뽕', 8000, null, 'https://example.com/items/jjamppong.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '중화루' LIMIT 1)),
(uuid_generate_v4(), '탕수육', '바삭한 탕수육(소)', 18000, 16000, 'https://example.com/items/tangsuyuk.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '중화루' LIMIT 1)),
(uuid_generate_v4(), '군만두', '고기가 꽉 찬 군만두', 6000, null, 'https://example.com/items/mandu.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'SIDE' LIMIT 1), (SELECT id FROM p_store WHERE name = '중화루' LIMIT 1));

-- 스시마스터 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '모듬초밥', '신선한 모듬초밥 10피스', 25000, null, 'https://example.com/items/sushi-set.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'SET' LIMIT 1), (SELECT id FROM p_store WHERE name = '스시마스터' LIMIT 1)),
(uuid_generate_v4(), '연어초밥', '노르웨이산 연어초밥 2피스', 8000, null, 'https://example.com/items/salmon.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '스시마스터' LIMIT 1)),
(uuid_generate_v4(), '우동', '따뜻한 우동', 9000, null, 'https://example.com/items/udon.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '스시마스터' LIMIT 1));

-- 파스타하우스 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '까르보나라', '크림 까르보나라 파스타', 14000, null, 'https://example.com/items/carbonara.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '파스타하우스' LIMIT 1)),
(uuid_generate_v4(), '알리오올리오', '마늘 올리브 오일 파스타', 13000, 11000, 'https://example.com/items/aglio.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '파스타하우스' LIMIT 1)),
(uuid_generate_v4(), '마르게리타 피자', '토마토와 모짜렐라 피자', 18000, null, 'https://example.com/items/margherita.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '파스타하우스' LIMIT 1)),
(uuid_generate_v4(), '시저샐러드', '신선한 시저샐러드', 9000, null, 'https://example.com/items/caesar.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'SIDE' LIMIT 1), (SELECT id FROM p_store WHERE name = '파스타하우스' LIMIT 1));

-- 굽네치킨 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '고추바사삭', '매콤한 고추바사삭 치킨', 19000, 17000, 'https://example.com/items/spicy-chicken.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '굽네치킨 강남점' LIMIT 1)),
(uuid_generate_v4(), '오리지널', '바삭한 오리지널 후라이드', 18000, null, 'https://example.com/items/original.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '굽네치킨 강남점' LIMIT 1)),
(uuid_generate_v4(), '치즈볼', '쫄깃한 치즈볼', 5000, null, 'https://example.com/items/cheese-ball.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'SIDE' LIMIT 1), (SELECT id FROM p_store WHERE name = '굽네치킨 강남점' LIMIT 1)),
(uuid_generate_v4(), '콜라 1.5L', '시원한 콜라', 2000, null, 'https://example.com/items/cola.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'DRINK' LIMIT 1), (SELECT id FROM p_store WHERE name = '굽네치킨 강남점' LIMIT 1));

-- 피자알볼로 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '페퍼로니 피자', '페퍼로니가 가득한 피자', 22000, 19900, 'https://example.com/items/pepperoni.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '피자알볼로' LIMIT 1)),
(uuid_generate_v4(), '콤비네이션 피자', '다양한 토핑의 피자', 24000, null, 'https://example.com/items/combination.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '피자알볼로' LIMIT 1)),
(uuid_generate_v4(), '감자튀김', '바삭한 감자튀김', 4000, null, 'https://example.com/items/fries.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'SIDE' LIMIT 1), (SELECT id FROM p_store WHERE name = '피자알볼로' LIMIT 1));

-- 쉐이크쉑 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '쉑버거', '시그니처 쉑버거', 8900, null, 'https://example.com/items/shackburger.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '쉐이크쉑 명동점' LIMIT 1)),
(uuid_generate_v4(), '스모크쉑', '베이컨이 들어간 버거', 10900, null, 'https://example.com/items/smokeshack.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '쉐이크쉑 명동점' LIMIT 1)),
(uuid_generate_v4(), '쉑쉐이크', '바닐라 쉐이크', 5900, null, 'https://example.com/items/shake.jpg', true, false, NOW(), NOW(), 'owner001', 'owner001', (SELECT id FROM p_item_category WHERE category_id = 'DRINK' LIMIT 1), (SELECT id FROM p_store WHERE name = '쉐이크쉑 명동점' LIMIT 1));

-- 스타벅스 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '아메리카노', '깔끔한 아메리카노', 4500, null, 'https://example.com/items/americano.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'DRINK' LIMIT 1), (SELECT id FROM p_store WHERE name = '스타벅스 역삼점' LIMIT 1)),
(uuid_generate_v4(), '카페라떼', '부드러운 카페라떼', 5000, null, 'https://example.com/items/latte.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'DRINK' LIMIT 1), (SELECT id FROM p_store WHERE name = '스타벅스 역삼점' LIMIT 1)),
(uuid_generate_v4(), '초코칩 쿠키', '달콤한 쿠키', 3000, null, 'https://example.com/items/cookie.jpg', true, false, NOW(), NOW(), 'owner002', 'owner002', (SELECT id FROM p_item_category WHERE category_id = 'DESSERT' LIMIT 1), (SELECT id FROM p_store WHERE name = '스타벅스 역삼점' LIMIT 1));

-- 타이푸드 메뉴
INSERT INTO p_item (id, name, description, price, sale_price, image, active, sold_out, created_at, updated_at, created_by, updated_by, category_id, store_id) VALUES
(uuid_generate_v4(), '팟타이', '태국식 볶음면', 12000, 10000, 'https://example.com/items/padthai.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '타이푸드' LIMIT 1)),
(uuid_generate_v4(), '똠양꿍', '새콤매콤 똠양꿍', 13000, null, 'https://example.com/items/tomyum.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'MAIN' LIMIT 1), (SELECT id FROM p_store WHERE name = '타이푸드' LIMIT 1)),
(uuid_generate_v4(), '망고스티키라이스', '달콤한 망고 디저트', 8000, null, 'https://example.com/items/mango.jpg', true, false, NOW(), NOW(), 'owner003', 'owner003', (SELECT id FROM p_item_category WHERE category_id = 'DESSERT' LIMIT 1), (SELECT id FROM p_store WHERE name = '타이푸드' LIMIT 1));

-- ============================================
-- 6. 주소 데이터 (p_address)
-- ============================================
INSERT INTO p_address (id, name, addr_road, addr_detail, addr_lat, addr_lng, is_default, memo, direction, entrance_password, user_id, created_at, updated_at, created_by, updated_by) VALUES
(uuid_generate_v4(), '집', '서울특별시 강남구 테헤란로 427', '위워크타워 15층', 37.5065892, 127.0538028, true, '문 앞에 놓아주세요', '엘리베이터 앞 왼쪽', null, 'customer001', NOW(), NOW(), 'customer001', 'customer001'),
(uuid_generate_v4(), '회사', '서울특별시 강남구 역삼로 234', '강남빌딩 3층', 37.4979502, 127.0276368, false, '경비실에 맡겨주세요', '정문 좌측', '1234#', 'customer001', NOW(), NOW(), 'customer001', 'customer001'),
(uuid_generate_v4(), '집', '서울특별시 송파구 올림픽로 300', '롯데월드타워 50층', 37.5133616, 127.1028327, true, null, null, null, 'customer002', NOW(), NOW(), 'customer002', 'customer002'),
(uuid_generate_v4(), '본가', '서울특별시 마포구 월드컵북로 396', '누리꿈스퀘어 10층', 37.5663329, 126.9019330, true, '초인종 눌러주세요', null, null, 'customer003', NOW(), NOW(), 'customer003', 'customer003');

-- ============================================
-- 시드 데이터 삽입 완료
-- ============================================

-- 참고: 
-- 1. 모든 사용자의 비밀번호는 "password123"입니다 (BCrypt 암호화됨)
-- 2. UUID는 PostgreSQL의 gen_uuid_generate_v4() 함수로 자동 생성됩니다
-- 3. 이미지 URL은 예시이므로 실제 사용 시 변경이 필요합니다
-- 4. 좌표(latitude, longitude)는 실제 서울 주요 지역 좌표를 사용했습니다
