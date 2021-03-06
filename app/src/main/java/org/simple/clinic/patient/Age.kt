package org.simple.clinic.patient

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant

@Parcelize
data class Age(
    val value: Int,
    val updatedAt: Instant
) : Parcelable
