UI가 없는 컴포넌트

앱을 백그라운드로 실행하기 위한 범용 진입점

UI 없이 오래 유지되는 작업을 처리한다.

## 역할

- 오래걸리는 작업
- UI랑 상호작용할 일 없는 작업
- 다른 컴포넌트를 대신하여 처리하는 작업

## 예시

- 음악 앱
- 네트워크 동기화

Service는 UI가 없으므로 UI가 필요한 경우에는 Notification이나 Activity를 사용해야 한다.

## Activity와의 관계

- Activity가 startService나 bindService 등으로 Service를 시작하거나 연결한다.
- Service는 Activity 가 사라져도 작업하는 경우나 Activity와 상호작용하는 패턴을 선택 가능하다

## Service의 타입

### Activity가 사라져도 작업하는 타입

- startService로 시작됨
- 이 작업(서비스의)이 끝날 때까지 계속 동작한다.

### 다른 무언가와 붙어서 상호작용하며 작업하는 타입

- bindService로 연결됨
- 누군가와 연결되어 있는 동안만 동작하는 service
- 다른 컴포넌트 or 앱이 해당 서비스의 함수/api를 직접 호출해서 사용한다.

## +alpha

서비스를 한 줄로 정리하면

- ui 없이도 오래 도는 작업이나 다른 컴포넌트를 위하여 백그라운드로 작업되는 컴포넌트
- startService → 서비스의 작업이 끝날때까지 실행한다.
- bindService → 다른 컴포넌트/앱이 나에게 붙어서 API 호출
