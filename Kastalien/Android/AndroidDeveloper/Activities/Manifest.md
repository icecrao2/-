

# 개념

- 앱 정체성·권한·컴포넌트 선언 문서

# 역할

### Activity 구동

- 아래와 같이 manifest > application > activity 순서 정의 및 activity 속성에 android:name=”activity클래스이름” 으로 구성하면 해당 activity 사용을 선언할 수 있다. (실행은 Intent를 통해서)

```jsx
<manifest ... >
  <application ... >
      <activity android:name=".ExampleActivi>ty" /
  <    ...
  /applic>ation .<.. 
  ...
>/manifest 
```

# 구성

### name

- 애플리케이션이 어떤 Activity를 구성시킬지 표현하는 속성이다.

```jsx
<manifest ... >
  <application ... >
      <activity android:name=".ExampleActivity" />
      ...
  </application ... >
  ...
</manifest >
```

### Intent filter

- activity 구동 방식에는 명시적/암시적 두가지 방법이 있다고 한다.
    - 명시적 방법은 activity클래스명을 알면 직접 호출할 수 있는 방법으로 보인다.
    - 암시적 방법은 activity의 카테고리나 데이터 등등의 정보를 통해 호출할 수 있는 방법으로 보인다.
        - 이 암시적 방법으로의 activity 실행을 위해 필요한것이 intent filter로 보인다.

```
<activity android:name=".ExampleActivity" android:icon="@drawable/app_icon">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

- 앱 내에서 activity 이동시에 암시적 방법을 쓸 일은 없을듯 하고 타 앱에 의한 실행같은 특수한 경우에만 가끔 사용될 기술로 보인다.

### uses-permission

- uses-permission 을 활용하여 권한을 설정하며 이 권한을 통해 Activity를 사용할 수 있는 Activity 제한한다.
    - 부모 activity가 uses-permission이 선언된 앱을 사용하기 위해서는 똑같은 uses-permission속성을 가진 permission 속성을 선언해줘야 한다.

```jsx
<manifest>
<activity android:name="...."
   android:permission=”com.google.socialapp.permission.SHARE_POST”/>

<manifest>
   <uses-permission android:name="com.google.socialapp.permission.SHARE_POST" />
</manifest>
```

### +Alpha

- 안드로이드 구조를 보면 앱과 앱간의 열고 닫음이 자유로운 구조인듯 하다.
    - https://developer.android.com/guide/components/fundamentals?hl=ko
        - 링크에서 언급된것 처럼 각각의 앱은 Linux의 사용자이고 각 앱은 각각의 vm을 통해 독립적으로 실행되지만 시스템이 사용자(앱)의 id를 가지고 있다.
        - menifest에 명시되는 항목들은 추측하건데 OS의 시스템에게 보여질 사용자(앱)의 Linux 시스템 정보인듯 하다.
            - 이 정보를 통해 앱과 앱이 서로를 열 수 있도록 되어 있는것 같다.(정확히는 앱이 여는것이 아니라 OS 시스템이 열고 태스크 백스택에 다른 Activity를 올리는 구조인듯 싶다)
