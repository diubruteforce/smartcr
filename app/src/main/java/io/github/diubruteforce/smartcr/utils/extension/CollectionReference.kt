package io.github.diubruteforce.smartcr.utils.extension

import com.google.firebase.firestore.CollectionReference

fun CollectionReference.whereActiveData() = this.whereEqualTo("isActive", true)