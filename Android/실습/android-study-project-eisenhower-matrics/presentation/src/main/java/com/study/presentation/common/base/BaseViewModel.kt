import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.presentation.common.base.UiEffect
import com.study.presentation.common.base.UiEvent
import com.study.presentation.common.base.UiOperation
import com.study.presentation.common.base.UiStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<
        EVENT : UiEvent,
        DATA,
        OPERATION : UiOperation,
        EFFECT : UiEffect,
        > : ViewModel() {

    protected val _uiStatus = MutableLiveData<UiStatus<DATA, OPERATION>>().apply {
        value = UiStatus.Idle(createInitialData())
    }
    val uiStatus: LiveData<UiStatus<DATA, OPERATION>> = _uiStatus
    protected abstract fun createInitialData(): DATA

    val currentData: DATA
        get() = when (val status = _uiStatus.value) {
            is UiStatus.Idle -> status.data ?: createInitialData()
            is UiStatus.Success -> status.data
            is UiStatus.Fail -> status.data ?: createInitialData()
            null -> createInitialData()
        }


    protected fun updateData(transform: (DATA) -> DATA) {
        val newData = transform(currentData)
        _uiStatus.value = when (val status = _uiStatus.value) {
            is UiStatus.Idle -> status.copy(data = newData)
            is UiStatus.Success -> status.copy(data = newData)
            is UiStatus.Fail -> status.copy(data = newData)
            null -> UiStatus.Idle(newData)
        }
    }

    protected fun setIdle(data: DATA? = null) {
        _uiStatus.value = UiStatus.Idle(data ?: currentData)
    }

    protected fun setSuccess(data: DATA? = null, operation: OPERATION) {
        _uiStatus.value = UiStatus.Success(data ?: currentData, operation)
    }

    protected fun setFail(
        message: String? = null,
        throwable: Throwable? = null,
        data: DATA? = null,
        operation: OPERATION
    ) {
        _uiStatus.value = UiStatus.Fail(
            message = message,
            data = data ?: currentData,
            throwable = throwable,
            operation = operation
        )
    }

    private val _effect = MutableSharedFlow<EFFECT>()
    val effect: SharedFlow<EFFECT> = _effect

    protected fun sendEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    fun onEvent(event: EVENT) {
        handleEvent(event)
    }

    protected abstract fun handleEvent(event: EVENT)
}
