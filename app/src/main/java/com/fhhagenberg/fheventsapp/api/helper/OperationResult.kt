package com.fhhagenberg.fheventsapp.api.helper

data class OperationResult(
    val success: Boolean,
    val errorMessage: String? = null
)