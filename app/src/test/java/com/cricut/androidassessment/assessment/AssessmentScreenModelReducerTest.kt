package com.cricut.androidassessment.assessment

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AssessmentScreenModelReducerTest {

    private lateinit var objectUnderTest: AssessmentScreenModelReducer

    private val testQuestion1 = Question.TrueFalse(id = "tf1", text = "Q1", correctAnswer = true)
    private val testQuestion2 = Question.MultipleChoice(
        id = "mc1",
        text = "Q2",
        options = listOf("A", "B"),
        correctAnswerIndex = 0
    )
    private val testQuestions = listOf(testQuestion1, testQuestion2)

    @Before
    fun setUp() {
        objectUnderTest = AssessmentScreenModelReducer()
    }

    @Test
    fun `createInitialState should set isLoading true and isError false`() {
        val initialState = objectUnderTest.createInitialState()
        assertTrue("Initial state should be loading", initialState.isLoading)
        assertFalse("Initial state should not be error", initialState.isError)
        assertTrue("Initial state should have empty questions", initialState.questions.isEmpty())
        assertTrue("Initial state should have empty answers", initialState.answers.isEmpty())
    }

    @Test
    fun `updateModelWithError should set isLoading false and isError true`() {
        val previousState = objectUnderTest.createInitialState()
        val errorState = objectUnderTest.updateModelWithError(previousState)
        assertFalse("Error state should not be loading", errorState.isLoading)
        assertTrue("Error state should be error", errorState.isError)
    }

    @Test
    fun `updateModelWithQuestions should update questions and set loading and error false`() {
        val previousState = objectUnderTest.createInitialState()
        val loadedState = objectUnderTest.updateModelWithQuestions(previousState, testQuestions)

        assertFalse("Loaded state should not be loading", loadedState.isLoading)
        assertFalse("Loaded state should not be error", loadedState.isError)
        assertEquals(
            "Loaded state should contain the questions",
            testQuestions,
            loadedState.questions
        )
        assertEquals("Current question index should be 0", 0, loadedState.currentQuestionIndex)
    }

    @Test
    fun `MapsToNextQuestion should increment index when possible`() {
        val previousState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = mapOf(testQuestion1.id to true),
            isLoading = false,
            isError = false
        )
        val nextState = objectUnderTest.navigateToNextQuestion(previousState)
        assertEquals("Index should increment", 1, nextState.currentQuestionIndex)
    }

    @Test
    fun `MapsToNextQuestion should not increment index at end or if unanswered`() {
        val endState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf(testQuestion1.id to true, testQuestion2.id to 0),
            isLoading = false, isError = false
        )
        assertEquals(
            "Index should not change at end",
            1,
            objectUnderTest.navigateToNextQuestion(endState).currentQuestionIndex
        )

        val unansweredState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = emptyMap(),
            isLoading = false, isError = false
        )
        assertEquals(
            "Index should not change if current question unanswered",
            0,
            objectUnderTest.navigateToNextQuestion(unansweredState).currentQuestionIndex
        )
    }

    @Test
    fun `MapsToPreviousQuestion should decrement index when possible`() {
        val previousState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf(),
            isLoading = false,
            isError = false
        )
        val prevState = objectUnderTest.navigateToPreviousQuestion(previousState)
        assertEquals("Index should decrement", 0, prevState.currentQuestionIndex)
    }

    @Test
    fun `MapsToPreviousQuestion should not decrement index at start`() {
        val startState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = mapOf(),
            isLoading = false, isError = false
        )
        assertEquals(
            "Index should not change at start",
            0,
            objectUnderTest.navigateToPreviousQuestion(startState).currentQuestionIndex
        )
    }


    @Test
    fun `saveAnswer should add the answer to the map`() {
        val previousState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = emptyMap(),
            isLoading = false, isError = false
        )
        val answer = true
        val questionId = testQuestion1.id

        val newState = objectUnderTest.saveAnswer(previousState, questionId, answer)

        assertTrue(
            "Answers map should contain the new answer key",
            newState.answers.containsKey(questionId)
        )
        assertEquals(
            "Answers map should contain the correct answer value",
            answer,
            newState.answers[questionId]
        )
    }

    @Test
    fun `saveAnswer should overwrite existing answer for the same question`() {
        val previousState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 0,
            answers = mapOf(testQuestion1.id to false),
            isLoading = false, isError = false
        )
        val newAnswer = true
        val questionId = testQuestion1.id

        val newState = objectUnderTest.saveAnswer(previousState, questionId, newAnswer)

        assertEquals("Answer should be updated", newAnswer, newState.answers[questionId])
        assertEquals("Answers map size should remain the same", 1, newState.answers.size)
    }

    @Test
    fun `updateModelWithCompletedAssessment should set isCompleted true`() {
        val previousState = AssessmentScreenModel(
            questions = testQuestions,
            currentQuestionIndex = 1,
            answers = mapOf(
                testQuestion1.id to true,
                testQuestion2.id to 0
            ),
            isLoading = false, isError = false, isCompleted = false
        )
        val completedState = objectUnderTest.updateModelWithCompletedAssessment(previousState)
        assertTrue("State should be completed", completedState.isCompleted)
    }
}