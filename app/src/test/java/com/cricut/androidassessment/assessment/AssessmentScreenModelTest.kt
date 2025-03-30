package com.cricut.androidassessment.assessment

import org.junit.Assert.*
import org.junit.Test

class AssessmentScreenModelTest {

    private val testQuestionTF = Question.TrueFalse(id = "tf1", text = "TF Q", correctAnswer = true)
    private val testQuestionMC = Question.MultipleChoice(
        id = "mc1",
        text = "MC Q",
        options = listOf("A", "B"),
        correctAnswerIndex = 0
    )
    private val testQuestions = listOf(testQuestionTF, testQuestionMC)

    @Test
    fun `currentQuestion should return correct question based on index`() {
        val modelAtStart = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            isLoading = false,
            isError = false
        )
        assertEquals("Should return first question", testQuestionTF, modelAtStart.currentQuestion)

        val modelAtEnd = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            isLoading = false,
            isError = false
        )
        assertEquals("Should return second question", testQuestionMC, modelAtEnd.currentQuestion)
    }

    @Test
    fun `currentQuestion should return null for invalid index`() {
        val modelInvalidIndex = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 2,
            isLoading = false,
            isError = false
        )
        assertNull("Should return null for out-of-bounds index", modelInvalidIndex.currentQuestion)

        val modelEmptyQuestions = AssessmentScreenModel(
            questions = emptyList(),
            currentQuestionIndex = 0,
            isLoading = false,
            isError = false
        )
        assertNull("Should return null if questions are empty", modelEmptyQuestions.currentQuestion)
    }

    @Test
    fun `canGoNext logic should be correct`() {
        val modelNotAnswered = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = emptyMap(),
            isLoading = false,
            isError = false
        )
        assertFalse("Cannot go next if current Q unanswered", modelNotAnswered.canGoNext)

        val modelAnswered = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = mapOf("tf1" to true),
            isLoading = false,
            isError = false
        )
        assertTrue("Can go next if current Q answered", modelAnswered.canGoNext)

        val modelAtEnd = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf("tf1" to true, "mc1" to 0),
            isLoading = false,
            isError = false
        )
        assertFalse("Cannot go next if at end", modelAtEnd.canGoNext)
    }

    @Test
    fun `canGoPrevious logic should be correct`() {
        val modelAtStart = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            isLoading = false,
            isError = false
        )
        assertFalse("Cannot go previous at index 0", modelAtStart.canGoPrevious)

        val modelNotAtStart = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            isLoading = false,
            isError = false
        )
        assertTrue("Can go previous if index > 0", modelNotAtStart.canGoPrevious)
    }

    @Test
    fun `canFinish logic should be correct`() {
        val modelPartialAnswers = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf("tf1" to true),
            isLoading = false,
            isError = false
        )
        assertFalse("Cannot finish with partial answers", modelPartialAnswers.canFinish)

        val modelEmpty = AssessmentScreenModel(
            questions = emptyList(),
            currentQuestionIndex = 0,
            answers = emptyMap(),
            isLoading = false,
            isError = false
        )
        assertFalse("Cannot finish with empty questions", modelEmpty.canFinish)

        val modelAllAnswered = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf("tf1" to true, "mc1" to 0),
            isLoading = false,
            isError = false
        )
        assertTrue("Can finish when all answered", modelAllAnswered.canFinish)
    }

    @Test
    fun `getCurrentAnswer should return correct answer or null`() {
        val modelWithAnswer = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = mapOf("tf1" to true),
            isLoading = false,
            isError = false
        )
        assertEquals(
            "Should return answer for current question",
            true,
            modelWithAnswer.getCurrentAnswer()
        )

        val modelWithoutAnswer = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf("tf1" to true),
            isLoading = false,
            isError = false
        )
        assertNull(
            "Should return null if current question not answered",
            modelWithoutAnswer.getCurrentAnswer()
        )
    }

    @Test
    fun `correctlyAnsweredCount should calculate score correctly`() {

        val modelNoAnswers = AssessmentScreenModel(
            questions = testQuestions,
            answers = emptyMap(),
            isLoading = false,
            isError = false
        )
        assertEquals("Score should be 0 with no answers", 0, modelNoAnswers.correctlyAnsweredCount)


        val modelMixedAnswers = AssessmentScreenModel(
            questions = testQuestions,
            answers = mapOf("tf1" to true, "mc1" to 1),
            isLoading = false,
            isError = false
        )
        assertEquals("Score should be 1", 1, modelMixedAnswers.correctlyAnsweredCount)


        val modelAllCorrect = AssessmentScreenModel(
            questions = testQuestions,
            answers = mapOf("tf1" to true, "mc1" to 0),
            isLoading = false,
            isError = false
        )
        assertEquals("Score should be 2", 2, modelAllCorrect.correctlyAnsweredCount)


        val modelOneMissing = AssessmentScreenModel(
            questions = testQuestions,
            answers = mapOf("mc1" to 0),
            isLoading = false,
            isError = false
        )
        assertEquals("Score should be 1", 1, modelOneMissing.correctlyAnsweredCount)
    }
}