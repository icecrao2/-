앱 사용중 여러 상황에서 Activity 간 이동이 발생할 수 있다.

이 때, 새로운 액티비티로부터 값을 반환받을지 말지 에 따라 startActivity() startActivityForResul() 메서드를 활용하여 새로운 액티비티를 실행시키고 두 경우 모두 Intent를 전달하여 Activity를 특정짓고 실행시킨다.

## startActivity()

```jsx
val intent = Intent(this, SignInActivity::class.java)
startActivity(intent)
```

## startActivityForResult()

startActivityForResult는 startActivity와는 다르게 반환값을 바라는 activity 실행 메서드이다.

startActivityForResult로 다른 액티비티로부터 받은 반환값은 onActivityResult 메서드를 통해 얻을 수 있다.

```jsx
class MyActivity : Activity() {
    // ...

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // When the user center presses, let them pick a contact.
            startActivityForResult(
                    Intent(Intent.ACTION_PICK,Uri.parse("content://contacts")),
                    PICK_CONTACT_REQUEST)
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            PICK_CONTACT_REQUEST ->
                if (resultCode == RESULT_OK) {
                    // A contact was picked. Display it to the user.
                    startActivity(Intent(Intent.ACTION_VIEW, intent?.data))
                }
        }
    }

    companion object {
        internal val PICK_CONTACT_REQUEST = 0
    }
}
```

### Activity 실행시 주의사항

startActivity/startActivityForResult는 여러 activity의 상태 변화를 발생시킨다.

그렇기 때문에 하나의 리소스에 두 activity의 각각의 생애주기 콜백 메서드에서 접근하면 문제가 발생하거나 시점이 꼬여 잘못된 데이터가 나올 수 있어서 android는 이 순서를 정의했다.

순서는 아래와 같다.

A activity가 B activity를 실행했을 경우

1. A - onPause
2. B - onCreate → onStart → onResume
3. A - onStop

이러한 순서를 잘 유의해서 동일한 디스크에 데이터를 잘 못 가져와 버그가 발생하는 경우가 없도록 해야한다.
