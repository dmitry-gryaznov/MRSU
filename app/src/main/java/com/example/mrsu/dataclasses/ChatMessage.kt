import com.example.mrsu.dataclasses.User
import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("User")
    val user: User,

    @SerializedName("IsTeacher")
    val isTeacher: Boolean,

    @SerializedName("CreateDate")
    val createDate: String,

    @SerializedName("Text")
    val text: String?
)
