package io.sprout.api.user.model.dto

data class TraineeSearchResponseDto(
    val userId: Long,
    val name: String,
    val nickname: String,
    val email: String,
    val phoneNumber: String,
    val campus: MutableList<Campus> = mutableListOf(),
    val course: MutableList<Course> = mutableListOf(),
    val memo: Memo?
){
    data class Campus(
        val campusId: Long,
        val name: String
    )

    data class Course(
        val courseId: Long,
        val name: String
    )

    data class Memo(
        val memoId: Long?,
        val content: String?
    )



    fun distinctCampus() {
        val distinctCampus = campus.distinct()
        campus.clear()
        campus.addAll(distinctCampus)
    }

}
