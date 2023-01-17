package com.android.testable.myapplication.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.android.testable.myapplication.api.Dog
import com.android.testable.myapplication.api.DownloadService
import com.android.testable.myapplication.storage.filesystem.ProviderFileHandler
import com.android.testable.myapplication.storage.preference.DownloadPreferencesWrapper
import com.android.testable.myapplication.storage.room.DogDao
import com.android.testable.myapplication.storage.room.DogEntity
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class DownloadRepositoryImplTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @InjectMocks
    lateinit var downloadRepositoryImpl: DownloadRepositoryImpl

    @Mock
    lateinit var downloadPreferencesWrapper: DownloadPreferencesWrapper

    @Mock
    lateinit var providerFileHandler: ProviderFileHandler

    @Mock
    lateinit var downloadService: DownloadService

    @Mock
    lateinit var dogDao: DogDao

    @Mock
    lateinit var dogMapper: DogMapper

    @Mock
    lateinit var executor: Executor

    @Mock
    lateinit var downloadCall: Call<ResponseBody>

    @Mock
    lateinit var dogCall: Call<Dog>
    private val fileName = "fileName"
    private val downloadUrl = "http://test.com/$fileName"
    private val numberOfResults = 5

    @Before
    fun setUp() {
        whenever(executor.execute(any())).thenAnswer {
            (it.arguments[0] as Runnable).run()
        }
        whenever(downloadPreferencesWrapper.getNumberOfResults()).thenReturn(numberOfResults)
        whenever(downloadService.getDogs(numberOfResults)).thenReturn(dogCall)
        whenever(downloadService.downloadFile(downloadUrl)).thenReturn(downloadCall)
    }

    @Test
    fun loadDogList_success() {
        val dogEntities = listOf(DogEntity(1, "url1"), DogEntity(2, "url2"))
        val dog = Dog("success", listOf("dog1", "dog2"))
        val uiDogs = listOf(DogUi("url1"), DogUi("url2"))
        val entityLiveData = MutableLiveData<List<DogEntity>>()
        whenever(dogDao.loadDogs()).thenReturn(entityLiveData)
        whenever(dogCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<Dog>).onResponse(dogCall, Response.success(dog))
        }
        whenever(dogMapper.mapServiceToEntity(dog)).thenReturn(dogEntities)
        for (i in uiDogs.indices) {
            whenever(dogMapper.mapEntityToUi(dogEntities[i])).thenReturn(uiDogs[i])
        }

        val result = downloadRepositoryImpl.loadDogList()
        result.observeForever {
            when (it) {
                is Result.Success -> {
                    Assert.assertEquals(uiDogs, it.data)
                }
                else -> {

                }
            }
        }
        entityLiveData.value = dogEntities

        verify(dogDao).deleteAll()
        verify(dogDao).insertDogs(dogEntities)
    }

    @Test
    fun loadDogList_serverError() {
        val entityLiveData = MutableLiveData<List<DogEntity>>()
        whenever(dogDao.loadDogs()).thenReturn(entityLiveData)
        whenever(dogCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<Dog>).onResponse(
                dogCall, Response.error(
                    400, ResponseBody.create(
                        MediaType.get("text/plain"), "test"
                    )
                )
            )
        }
        val result = downloadRepositoryImpl.loadDogList()
        verify(dogDao, never()).deleteAll()
        Assert.assertTrue(result.value!! is Result.Error)
    }

    @Test
    fun loadDogList_genericError() {
        val entityLiveData = MutableLiveData<List<DogEntity>>()
        whenever(dogDao.loadDogs()).thenReturn(entityLiveData)
        whenever(dogCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<Dog>).onFailure(dogCall, RuntimeException("Test"))
        }
        val result = downloadRepositoryImpl.loadDogList()
        verify(dogDao, never()).deleteAll()
        Assert.assertTrue(result.value!! is Result.Error)
    }

    @Test
    fun downloadFile_success() {
        val content = "content"
        val body = ResponseBody.create(MediaType.get("text/plain"), content)
        body.byteStream()
        whenever(downloadCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<ResponseBody>).onResponse(
                downloadCall,
                Response.success(body)
            )
        }

        val result = downloadRepositoryImpl.downloadFile(downloadUrl)

        Assert.assertEquals(Result.Success(Unit), result.value!!)
    }

    @Test
    fun downloadFile_errorDuringDownload() {

        val content = "content"
        val body = ResponseBody.create(MediaType.get("text/plain"), content)
        body.byteStream()
        whenever(downloadCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<ResponseBody>).onResponse(
                downloadCall,
                Response.success(body)
            )
        }
        whenever(providerFileHandler.writeStream(eq(fileName), any()))
            .thenThrow(RuntimeException("Test"))

        val result = downloadRepositoryImpl.downloadFile(downloadUrl)

        Assert.assertTrue(result.value!! is Result.Error)
    }

    @Test
    fun downloadFile_serverError() {
        val content = "content"
        val body = ResponseBody.create(MediaType.get("text/plain"), content)
        body.byteStream()
        whenever(downloadCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<ResponseBody>).onResponse(
                downloadCall,
                Response.error(400, body)
            )
        }

        val result = downloadRepositoryImpl.downloadFile(downloadUrl)

        Assert.assertTrue(result.value!! is Result.Error)
    }

    @Test
    fun downloadFile_genericError() {
        val content = "content"
        val body = ResponseBody.create(MediaType.get("text/plain"), content)
        body.byteStream()
        whenever(downloadCall.enqueue(any())).thenAnswer {
            (it.arguments[0] as Callback<ResponseBody>).onFailure(
                downloadCall,
                RuntimeException("Test")
            )
        }

        val result = downloadRepositoryImpl.downloadFile(downloadUrl)

        Assert.assertTrue(result.value!! is Result.Error)
    }
}