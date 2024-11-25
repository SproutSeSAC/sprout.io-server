package io.sprout.api.utils

import java.util.*
import kotlin.random.Random

object NicknameGenerator {
    private val NOUNS = arrayOf("꽃", "나무", "바다", "하늘", "별", "빛", "음악", "사랑", "행복", "꿈", "모래", "강", "바람", "햇살", "눈", "비", "숲", "감성", "평화", "세상")
    private val ADJECTIVES = arrayOf("화려한", "창의적인", "열정적인", "자유로운", "활기찬", "격렬한", "쾌활한", "우아한", "현명한", "우아한", "정신없는", "신비로운", "풍부한", "안정된", "다채로운", "유쾌한", "진실한", "영리한", "열정적인", "차분한")

    fun generate(): String {
//        val adjectiveIndex = Random.nextInt(ADJECTIVES.size)
//        val nounIndex = Random.nextInt(NOUNS.size)
//        val nickname = "${ADJECTIVES[adjectiveIndex]} ${NOUNS[nounIndex]}"
//        val hashtag = Random.nextInt(1000, 1000000)
//        return nickname + hashtag
        return UUID.randomUUID().toString()
    }
}