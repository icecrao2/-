package com.study.presentation.feature.eisenhower.di

import com.study.presentation.feature.eisenhower.viewmodel.AddTodoViewModel
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoViewModel
import com.study.presentation.feature.eisenhower.viewmodel.EisenhowerMatricsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val eisenhowerFeatureModule = module {
    viewModel { EisenhowerMatricsListViewModel(get()) }
    viewModel { EditTodoViewModel(get()) }
    viewModel { AddTodoViewModel(get()) }
}