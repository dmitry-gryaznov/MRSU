data class StudentSemester(
    val RecordBooks: List<RecordBook>,
    val Year: String,
    val Period: Int
)

data class RecordBook(
    val Cod: String,
    val Number: String,
    val Faculty: String,
    val Disciplines: List<Disc>
)

data class Disc(
    val Id: Int,
    val Title: String,
    val PeriodString: String,
    val Faculty: String
)
