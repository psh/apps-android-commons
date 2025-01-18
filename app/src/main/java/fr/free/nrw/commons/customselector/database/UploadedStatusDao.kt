package fr.free.nrw.commons.customselector.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * UploadedStatusDao for Custom Selector.
 */
@Dao
interface UploadedStatusDao {
    /**
     * Insert into uploaded status.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(uploadedStatus: UploadedStatusEntity)

    /**
     * Update uploaded status entry.
     */
    @Update
    suspend fun update(uploadedStatus: UploadedStatusEntity)

    /**
     * Delete uploaded status entry.
     */
    @Delete
    suspend fun delete(uploadedStatus: UploadedStatusEntity)

    /**
     * Query uploaded status with image sha1.
     */
    @Query("SELECT * FROM uploaded_table WHERE imageSHA1 = (:imageSHA1) ")
    suspend fun getFromImageSHA1(imageSHA1: String): UploadedStatusEntity?

    /**
     * Query uploaded status with modified image sha1.
     */
    @Query("SELECT * FROM uploaded_table WHERE modifiedImageSHA1 = (:modifiedImageSHA1) ")
    suspend fun getFromModifiedImageSHA1(modifiedImageSHA1: String): UploadedStatusEntity?

    /**
     * Check whether the imageSHA1 is present in database
     */
    @Query("SELECT COUNT() FROM uploaded_table WHERE imageSHA1 = (:imageSHA1) AND imageResult = (:imageResult) ")
    suspend fun findByImageSHA1(imageSHA1: String, imageResult: Boolean): Int

    /**
     * Check whether the modifiedImageSHA1 is present in database
     */
    @Query(
        "SELECT COUNT() FROM uploaded_table WHERE modifiedImageSHA1 = (:modifiedImageSHA1) AND modifiedImageResult = (:modifiedImageResult) ",
    )
    suspend fun findByModifiedImageSHA1(modifiedImageSHA1: String, modifiedImageResult: Boolean): Int
}
