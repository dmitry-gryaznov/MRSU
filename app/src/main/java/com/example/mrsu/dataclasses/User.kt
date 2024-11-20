package com.example.mrsu.dataclasses


import com.example.mrsu.dataclasses.Role
import com.example.mrsu.dataclasses.Photo
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("Email")
    val email: String,

    @SerializedName("EmailConfirmed")
    val emailConfirmed: Boolean,

    @SerializedName("EnglishFIO")
    val englishFio: String?,

    @SerializedName("TeacherCod")
    val teacherCod: String?,

    @SerializedName("StudentCod")
    val studentCod: String?,

    @SerializedName("BirthDate")
    val birthDate: String?,

    @SerializedName("AcademicDegree")
    val academicDegree: String?,

    @SerializedName("AcademicRank")
    val academicRank: String?,

    @SerializedName("Roles")
    val roles: List<Role>,

    @SerializedName("Id")
    val id: String,

    @SerializedName("UserName")
    val userName: String,

    @SerializedName("FIO")
    val fio: String,

    @SerializedName("Photo")
    val photo: Photo
)
