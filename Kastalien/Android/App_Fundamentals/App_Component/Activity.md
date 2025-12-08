## Activity

- 사용자와 상호작용하는 시작점
- 사용자 인터페이스가 있는 단일 화면을 나타낸다.
- Activity는 사용자의 관심을 끌면서(resume 단계) Activity가 포함된 process가 유지될 수 있도록 해준다.
- Activity는 Stop되었어도 언제든지 사용자 관심을 받을 수 있는 요소로 인식되어 같은 Cached Process 내에서도 더 높은 중요도를 갖게 된다.
- onSaveInstanceState 등을 통해 프로세스가 죽어도 화면 복원이 수월해지게 돕는다.
- 앱 간 사용자 흐름을 구현하는 통로로 사용된다.
    - 여러 앱을 실행하기 위해서는 Intent+Activity 구조가 필수적
    - 사용자가 여러 앱을 사용중이더라도 여러 앱의 Activity를 Task BackStack에 함께 보관하여 앱을 오간다기보다는 하나의 앱에 있다라는 느낌을 줄 수 있음 ⇒ 이래서 준형님이 여러 Activity로 작업하신듯!
