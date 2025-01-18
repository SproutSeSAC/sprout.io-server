package io.sprout.api.project.model.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class ProjectResponseDto @QueryProjection constructor(
    val id: Long,
    val title: String,
    val description: String,
    val recruitmentCount: Int,
    val meetingType: String,
    val contactMethod: String,
    val recruitmentStart: LocalDate,
    val recruitmentEnd: LocalDate,
    val pType: String,
    val positionNames: List<String>,
//    val techStackNames: List<String>,
    val techStacks: List<TechStacks>,
    val isScraped: Boolean,
    val viewCount: Int
){
   fun toDistinct(): ProjectResponseDto{
       return ProjectResponseDto(
           id = this.id,
           title = this.title,
           description = this.description,
           recruitmentCount = this.recruitmentCount,
           meetingType = this.meetingType,
           contactMethod = this.contactMethod,
           recruitmentStart = this.recruitmentStart,
           recruitmentEnd = this.recruitmentEnd,
           pType = this.pType,
           positionNames = this.positionNames.distinct(),
           techStacks = this.techStacks.distinct(),
//           techStackNames = this.techStackNames.distinct(),
           isScraped = this.isScraped,
           viewCount = this.viewCount
       )
   }

    data class TechStacks(
        val name: String,
        val imageUrl: String
    )

}