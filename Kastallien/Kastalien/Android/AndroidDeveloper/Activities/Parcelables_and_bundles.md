Parcelable 과 Bundle은 프로세스 경계를 넘어서 데이터를 교환할때 사용된다.

### 용어

1. Binder
    1. 안드로이드에서 컴포넌트(서비스, 액티비티, 프로세스…)간 데이터 통신은 모두 이 Binder를 통해서 이뤄진다.
2. Parcel
    1. Binder가 취급하는 데이터 형식은 Parcel이다.
3. Parcelable
    1. 특정 데이터 타입을 Parcel 형식으로 설명 할 수 있게 만들어주는 인터페이스이다.
4. Bundle
    1. Activity간 데이터 전달시 사용되며 Key-Value 쌍으로  이루어져 있다.
    2. Activity간 통신 또한 Binder를 활용함으로 Parcelable을 구현하여 Binder를 통해 다른 액티비티에 전달 될 수 있게 만들어졌다.

### 용도

1. IPC/Binder
    1. IPC/Binder는 프로세스끼리 데이터를 주고받을수 있게 해주는 시스템이다.
    2. 이때 데이터를 싸서 보내는 형식이 parcel이다.
    3. parcel을 개발자가 쓰기 쉽게 추상화한게 parcelable, bundle이다.
2. Activity 간 데이터 전달
    1. startActivity 로 다른 액티비티를 백스탭 상단에 올릴때 putExtra를 통해 데이터를 보내는데 이 값들이 내부적으로 bundle ⇒ parcel 로 직렬화되어 전달됨
3. 구성 변경시 일시적인 상태 저장
    1. Bundle에서 일시적으로 상태를 저장하고 있음(onSaveInstanceState)

### 주의점

1. Activity 간 통신에는 괜찮지만 Process간 통신시에는 사용자 지정 Parcelable 클래스를 상용하지 말아라
    1. Parcelable은 “클래스 이름 + CREATOR” 기반으로 역직렬화한다.
    2. A 프로세스에서 MyClass 이름이라는 Parcelable 클래스를 보내면 B 프로세스에도 동일한 이름/필드명/필드구조에 클래스가 있어야 한다.
    3. 요약하면 프로세스간 통신은 서로 다른 프로세스이니 각각 VM을 가지고 있고 그 안에 정의된 내용도 다르니 시스템 정의된 클래스가 아닌 다른 타입들을 보내면 상대 VM이 그것을 모를 수 있어서 터질 수 있다. 라는 말이다.
