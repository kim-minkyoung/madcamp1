## TREASURES - 내 최애들만 따로 모아보자!

---

### Outline

<img width="614" alt="스크린샷 2024-07-03 오후 4 02 47" src="https://github.com/kim-minkyoung/madcamp1/assets/105150339/d1c806b8-6e7c-4add-8e9b-5ba542a92cfe">

**TREASURES - 내 최애들만 따로 모아보자!** 는 따로 모아 보고 싶은 📞**전화번호부**, 📷**사진**, 🏢**장소**를 한 번에 저장할 수 있는 아카이브 앱입니다.

---

### Team
김민경: https://github.com/kim-minkyoung

진유하: https://github.com/yjbigbrr

---

### Tech Stack
**Front-end** : Kotlin

**IDE** : Android Studio

---

### About
📞 **전화번호부: 좋아하는/자주 연락하는 사람들의 전화번호만 따로 모아 봐요.**

**수많은 연락처가 전화번호부에 저장되어 있지만, 사실 연락하는 사람들은 한정적이시진 않으신가요?**
- 즐겨 찾는 전화번호: 실제 디바이스의 전화번호부에 즐겨찾기 되어 있는 목록을 불러옵니다.
    - 연락처 검색: 이름으로 연락처를 검색할 수 있습니다.
    - 즐겨찾기 설정/해제: 각 연락처의 별을 누르면 즐겨찾기 추가/삭제를 할 수 있습니다. 이는 실시간으로 실제 디바이스의 전화번호부에도 반영됩니다.
    - 연락처 상세정보: 각 연락처를 클릭하면 연락처 정보를 자세하게 볼 수 있습니다.
- 모두 보기: 즐겨찾기로 설정해두지 않은 사람들의 전화번호 목록까지 모두 볼 수 있습니다.
    - 이 탭에서도 연락처 검색, 즐겨찾기 설정/해제, 연락처 상세정보 기능 모두 동일하게 구현됩니다.

📷 **사진: 좋아하는 사진들만 따로 모아 봐요.**

**너무 많은 사진들이 갤러리에 있지만, 특히나 아끼는 사진들을 따로 모아 보고 싶진 않으신가요?**
- 사진 추가하기: 좋아하는 사진을 추가하여 모아둔 사진만 따로 볼 수 있습니다.
- 사진 크게 보기: 추가한 사진을 클릭하면 사진이 크게 보입니다.
- 사진 선택 및 삭제: 사진 선택 버튼 클릭 → 삭제하고 싶은 사진(들) 선택 → 삭제 기능이 구현되어 있습니다.

🏢 **장소: 좋아하는 장소들만 따로 모아 봐요.**

**나만 아는 인생 맛집이나 인상 깊었던 여행지, 마음속에 하나쯤 품고 있지 않으신가요?**
- 현재 주소 로드: “장소” 탭에 처음 들어오면, 지도는 현 위치를 중심으로 보입니다.
- 저장하고 싶은 장소 검색 후 저장: 정확한 주소 또는 장소명, 어떤 형태로 검색하든 저장 버튼을 누르면 해당 장소를 정확히 찾아 저장할 수 있습니다.
- 지도에서 장소 클릭 후 저장: 지도에서 장소를 클릭하면, 그 장소를 저장할 수 있습니다.
- 저장된 장소들 보기: 아래의 bottomSheet를 끌어올리면 저장된 주소들을 볼 수 있습니다.
    - 저장된 장소 검색: bottomSheet 내부의 검색창에 저장된 장소를 검색하면 장소를 불러올 수 있습니다.

---

### Preview
**시작 화면**

![스플레시](https://github.com/kim-minkyoung/madcamp1/assets/105150339/ec128c2a-5596-4cb7-8756-e4a9acd90433)


📞 **전화번호부**

![Tab1_즐겨찾기 삭제](https://github.com/kim-minkyoung/madcamp1/assets/105150339/22127b96-a4c9-437d-8c8e-7746094f5d25)
![Tab1_연락처추가](https://github.com/kim-minkyoung/madcamp1/assets/105150339/4650a817-661d-428f-beae-8c0d2fa8a044)
![Tab1_연락처검색](https://github.com/kim-minkyoung/madcamp1/assets/105150339/51fad625-2ca7-4d80-b5ea-099d01b6abb6)

즐겨찾기 삭제 / 연락처 추가 / 연락처 검색



📷 **사진**

![Tab2_사진삭제](https://github.com/kim-minkyoung/madcamp1/assets/105150339/7ba74b38-fc2f-44b8-b109-a1ccdfc1457b)
![Tab2_사진추가](https://github.com/kim-minkyoung/madcamp1/assets/105150339/07fd4da7-8a25-4cbf-a506-645ce8b3d8a9)
![Tab2_이미지확대](https://github.com/kim-minkyoung/madcamp1/assets/105150339/b9d72240-7472-4d08-a8cc-5b41b738904f)

사진 삭제 / 사진 추가 / 이미지 확대


🏢 **장소**

![Tab3_마커추가](https://github.com/kim-minkyoung/madcamp1/assets/105150339/4d942398-a21d-48b7-b112-1dd745483617)
![Tab3_장소삭제](https://github.com/kim-minkyoung/madcamp1/assets/105150339/d30326b4-623a-4b78-a29e-50abeb2d4ad3)

마커 추가 / 장소 삭제

![Tab3_장소추가](https://github.com/kim-minkyoung/madcamp1/assets/105150339/5ca57c5a-b48c-4cee-a9bd-c8f71022afaa)
![Tab3_저장장소 검색](https://github.com/kim-minkyoung/madcamp1/assets/105150339/3f9b5ecc-4348-478e-abd6-7902beea4e4c)

장소 추가 / 저장 장소 검색

---

### Technical Issues we’ve met
1. Clean Code 작성 - MVVM Architecture 채택
- `상위 Fragment(즐겨찾기 전화번호만 보기)` - `하위 Activity(모든 전화번호 보기)`로 페이지가 구성된 상황에서 전화번호를 두 페이지 모두에서 load하는 것이 낭비라는 생각을 하게 됐다.
- 전화번호를 불러오는 파일을 따로 만들어 관리하는 게 어떨까 생각하게 되었고, `ContactRepository` 파일을 따로 만들어 각 컴포넌트 별로 관심사를 분리했다.
- 체계가 명확했으면 좋겠다고 생각하여 여러 research를 해본 끝에, MVVM Architecture을 채택하는 것이 좋겠다고 판단했다.
    - https://velog.io/@201/mvvmarchitecture
- 이후 다른 코드(특히 지도를 다룰 때) 관심사가 분리되어 있어 유지보수하기 좋았음. + 빌드 속도 빠름
    

2. 상위 페이지와 하위 페이지 간의 통신
- `ContactAllActivity`에서 즐겨찾기 상태를 변경하고 `Tab1Fragment`로 돌아왔을 때 변경 사항이 올바르게 반영되지 않는 문제 발생
- 첫 번째 문제: findFragmentByTag 미사용
- 두 번째 문제: `ContactRepository`에서 즐겨찾기 상태가 변경되면 데이터 리스트를 다시 로드하지 않음
- 세 번째 문제: `ContactAllActivity`에서 `onDestroy` 또는 `onPause`에서 `notifyTab1Fragment` 호출하지 않음

3. 네이버 지도 API 사용
- 처음 써보는 API라 서툴러서 많이 헤맸다.
    - dependencies 설정
    - 공식 문서에서 설명한 대로 api를 요청하고, 올바른 JSON 형식으로 응답 받기 (특히 reverse geocoding)
 
+) 하드코딩 X, 코드 최적화에 신경을 많이 썼다.
