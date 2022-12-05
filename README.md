# 

# 사전과제

## 코로나19 예방접종센터 지도 서비스

### 3일동안 진행했던 사전과제

요구사항

- 활용기술
    - Language
        - Kotlin
    - Design Pattern
        - MVVM
    - UI Layout
        - XML or Jetpack Compose
    - Network
        - Retrofit
    - jetpack
        - ViewBinding
        - DataBinding
        - etc ..
    - Naver Map or Google Map
        - • Naver Map 추천
    - DI
        - Hilt
    - 비동기
        - RxKotlin or Coroutine Flow
        - *Coroutine Flow* *추천*
- 요구사항
    - Splash
        - ProgressBar
            - 2초에 걸쳐 100%가 되도록 로딩바 구현
            - 단, API 데이터 저장이 완료되지 않았다면 80%에서 대기
                - 저장이 완료되면 0.4초 걸쳐 100%를 만든 후 Map 화면으로 이동
        - API를 통해 1페이지(page)에 10개(perPage) 씩 순서대로 10개 페이지 호출(총 100개)하여 데이터 저장
            - DataStore, ROOM, safeArgs(Jetpack NavigationComponent) 3가지 방식 중 하나
        - 저장이 완료되면 Map 화면으로 이동
    - Map
        - 마커 생성
            - 저장된 리스트의 데이터를 통해 마커 생성
            - CenterType에 따라 마커 색상 구분
        - 마커 클릭
            - 해당 마커로 지도 카메라 이동
            - 해당 마커의 정보를 정보안내창에 표시
            - 선택된 상태에서 같은 마커를 다시 선택하는 경우 선택 해제
        - 정보안내창
            - Visibility
                - 마커가 선택된 경우 visible, 아닌 경우 gone
            - 선택된 마커의 정보(Response Data)를 표시
                - DataBinding
            - 표시 항목
                - 디자인
                    - 자유 디자인
                - 표시 데이터
                    - **address**
                    - **centerName**
                    - **facilityName**
                    - **phoneNumber**
                    - **updatedAt**
        - 현재 위치 버튼
            - 버튼 클릭 시 현재 위치로 이동
                - (Map Library 내 UI가 아닌 직접 버튼 생성)
