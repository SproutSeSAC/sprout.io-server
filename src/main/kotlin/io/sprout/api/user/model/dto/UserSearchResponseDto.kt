package io.sprout.api.user.model.dto

data class UserSearchResponseDto(
    val userId: Long,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val campus: MutableList<Campus> = mutableListOf(),
    val course: MutableList<Course> = mutableListOf(),
){
    var domains: MutableList<Domain> = mutableListOf()
    var jobs: MutableList<Job> = mutableListOf()
    var techStacks: MutableList<TechStack> = mutableListOf()

    data class Campus(
        val campusId: Long,
        val name: String
    )

    data class Course(
        val courseId: Long,
        val name: String
    )

    data class Domain(
        val domainId: Long,
        val name: String
    )

    data class TechStack(
        val techStackId: Long,
        val name: String
    )

    data class Job(
        val jobId: Long,
        val name: String
    )

    fun distinctCampus() {
        val distinctCampus = campus.distinct()
        campus.clear()
        campus.addAll(distinctCampus)
    }

}
