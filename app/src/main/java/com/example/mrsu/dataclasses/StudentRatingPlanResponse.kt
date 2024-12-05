import com.google.gson.JsonElement

data class StudentRatingPlanResponse(
    val MarkZeroSession: MarkZeroSession?,
    val Sections: List<Section>
)

data class MarkZeroSession(
    val Id: Int?,
    val Ball: Double?,
    val CreatorId: String,
    val CreateDate: String
)

data class Section(
    val ControlDots: List<ControlDot>,
    val SectionType: Int,
    val Id: Int,
    val Order: Int,
    val Title: String,
    val Description: String?,
    val CreatorId: String,
    val CreateDate: String
)

data class ControlDot(
    val Mark: Mark?,
    val Report: Report?, // Updated to handle the object structure
    val Id: Int,
    val Order: Int,
    val Title: String,
    val Date: String?,
    val MaxBall: Double,
    val IsReport: Boolean,
    val IsCredit: Boolean,
    val CreatorId: String,
    val CreateDate: String,
    val TestProfiles: List<Any>
)

data class Mark(
    val Id: Int?,
    val Ball: Double?,
    val CreatorId: String,
    val CreateDate: String
)

data class Report(
    val Id: Int,
    val CreateDate: String,
    val DocFile: DocFile?
)

data class DocFile(
    val Id: String,
    val CreatorId: String,
    val Title: String,
    val FileName: String,
    val MIMEtype: String,
    val Size: Int,
    val Date: String,
    val URL: String
)
