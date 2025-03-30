package com.cricut.androidassessment.assessment

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AssessmentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var assessmentQuestionsRepository: AssessmentQuestionsRepository
    private lateinit var assessmentScreenModelReducer: AssessmentScreenModelReducer
    private lateinit var reducerSpy: AssessmentScreenModelReducer

    private lateinit var objectUnderTest: AssessmentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        assessmentQuestionsRepository = AssessmentQuestionsRepository()
        assessmentScreenModelReducer = AssessmentScreenModelReducer()
        reducerSpy = spyk(assessmentScreenModelReducer)
        objectUnderTest = AssessmentViewModel(assessmentQuestionsRepository, reducerSpy)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading from real reducer`() = runTest(testDispatcher.scheduler) {
        val expectedInitialState = assessmentScreenModelReducer.createInitialState()
        assertEquals(expectedInitialState, objectUnderTest.observableModel.value)
    }

    @Test
    fun `onStart should eventually load real questions and update state via spy`() = runTest(testDispatcher.scheduler) {
        val initialState = objectUnderTest.observableModel.value
        objectUnderTest.onStart()

        val loadedState = objectUnderTest.observableModel.first { !it.isLoading }

        assertFalse(loadedState.isLoading)
        assertFalse(loadedState.isError)
        assertFalse(loadedState.questions.isEmpty())
        assertEquals(2, loadedState.questions.size)

        val slot = slot<AssessmentScreenModel>()
        verify { reducerSpy.updateModelWithQuestions(capture(slot), any()) }
        assertEquals(initialState, slot.captured)
    }

    @Test
    fun `onStart should only trigger load once`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        objectUnderTest.observableModel.first { !it.isLoading }

        objectUnderTest.onStart()
        testDispatcher.scheduler.advanceTimeBy(100)
        objectUnderTest.onStart()
        testDispatcher.scheduler.advanceTimeBy(100)

        verify(exactly = 1) { reducerSpy.updateModelWithQuestions(any(), any()) }
        verify(exactly = 0) { reducerSpy.updateModelWithError(any()) }
    }


    @Test
    fun `onNextQuestion should call reducer spy navigateToNextQuestion`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        val loadedState = objectUnderTest.observableModel.first { !it.isLoading && it.questions.isNotEmpty() }
        val firstQuestionId = loadedState.questions.first().id

        objectUnderTest.onTrueFalseAnswerSelected(true)
        val answeredState = objectUnderTest.observableModel.first { it.answers.containsKey(firstQuestionId) }

        objectUnderTest.onNextQuestion()

        verify { reducerSpy.navigateToNextQuestion(match { it.answers.containsKey(firstQuestionId) }) }
    }

    @Test
    fun `onPreviousQuestion should call reducer spy navigateToPreviousQuestion`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        val loadedState = objectUnderTest.observableModel.first { !it.isLoading && it.questions.isNotEmpty() }
        val firstQuestionId = loadedState.questions.first().id
        objectUnderTest.onTrueFalseAnswerSelected(true)
        objectUnderTest.observableModel.first { it.answers.containsKey(firstQuestionId) }
        objectUnderTest.onNextQuestion()
        val stateAtIndex1 = objectUnderTest.observableModel.first { it.currentQuestionIndex == 1 }

        objectUnderTest.onPreviousQuestion()

        verify { reducerSpy.navigateToPreviousQuestion(stateAtIndex1) }
    }

    @Test
    fun `onTrueFalseAnswerSelected should call reducer spy saveAnswer`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        val loadedState = objectUnderTest.observableModel.first { !it.isLoading && it.questions.isNotEmpty() }
        val questionId = loadedState.currentQuestion?.id
        assertNotNull(questionId)

        val answer = true
        objectUnderTest.onTrueFalseAnswerSelected(answer)

        verify { reducerSpy.saveAnswer(loadedState, questionId!!, answer) }
    }

    @Test
    fun `onMultipleChoiceAnswerSelected should call reducer spy saveAnswer`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        val loadedState = objectUnderTest.observableModel.first { !it.isLoading && it.questions.isNotEmpty() }
        val questionId = loadedState.currentQuestion?.id
        assertNotNull(questionId)

        val answerIndex = 0
        objectUnderTest.onMultipleChoiceAnswerSelected(answerIndex)

        verify { reducerSpy.saveAnswer(loadedState, questionId!!, answerIndex) }
    }


    @Test
    fun `onFinishAssessment should call reducer spy updateModelWithCompletedAssessment when canFinish is true`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        val loadedState = objectUnderTest.observableModel.first { !it.isLoading && it.questions.size == 2 }
        val q1Id = loadedState.questions[0].id
        val q2Id = loadedState.questions[1].id

        objectUnderTest.onTrueFalseAnswerSelected(true)
        objectUnderTest.observableModel.first { it.answers.containsKey(q1Id) }
        objectUnderTest.onNextQuestion()
        objectUnderTest.observableModel.first { it.currentQuestionIndex == 1 }
        objectUnderTest.onMultipleChoiceAnswerSelected(0)
        objectUnderTest.observableModel.first { it.answers.containsKey(q2Id) }

        objectUnderTest.onFinishAssessment()

        verify { reducerSpy.updateModelWithCompletedAssessment( match { it.answers.size == 2 })}
        assertTrue(objectUnderTest.observableModel.value.isCompleted)
    }

    @Test
    fun `onFinishAssessment should NOT call reducer spy when canFinish is false`() = runTest(testDispatcher.scheduler) {
        objectUnderTest.onStart()
        objectUnderTest.observableModel.first { !it.isLoading && it.questions.isNotEmpty() }

        objectUnderTest.onFinishAssessment()

        verify(exactly = 0) { reducerSpy.updateModelWithCompletedAssessment(any()) }
        assertFalse(objectUnderTest.observableModel.value.isCompleted)
    }
}