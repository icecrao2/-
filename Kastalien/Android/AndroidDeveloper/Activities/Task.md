## Task 정의

사용자가 어떤 일을 하는동안 이어지는 Activity 묶음

## Back Stack 정의

Task 안에서 열린 액티비티가 쌓이는 스택(LIFO).

## 주의사항

- Android 11 이하에서는 Activity가 push 되었을 경우 백스택 하단으로 밀려난 Activity는 콜드 상태 즉 종료상태가 된다.
- Android 12 이상에서는 Activity가 push 되었을 경우 백스택 하단으로 밀려난 Activity는 웜 상태로 백그라운드에 있는 상태가 된다.

## +alpha

- Task와 BackStack 의 존재 유무로 인해 각각의 화면을 activity로 만들 경우 이전 화면을 BackStack에서 알아서 기억해주니 네비게이션 작업이 많이 줄어들 것으로 생각됨
- 글에서 언급된 내용 중 여러 Activity에서 동일한 Activity에 접근이 가능하고 그로 인해 화면이동이 A → C → B → C → D → C 되었을 경우에도 Back Stack은 화면 이동 그대로 생성되게 된다.
    - 이 말로 추측해보면 Activity로 생성되는 화면은 결국 전부 인스턴스화되어 있고 같은 Activity라고 해도 서로 독립적으로 존재하는것이다.
