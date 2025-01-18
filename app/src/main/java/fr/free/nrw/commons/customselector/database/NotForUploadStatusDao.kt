package fr.free.nrw.commons.customselector.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotForUploadStatusDao {
    /**
     * Insert into Not For Upload status.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notForUploadStatus: NotForUploadStatusEntity)

    /**
     * Delete Not For Upload status entry.
     */
    @Delete
    suspend fun delete(notForUploadStatus: NotForUploadStatusEntity)

    /**
     * Check whether the imageSHA1 is present in database
     */
    @Query("SELECT COUNT() FROM images_not_for_upload_table WHERE imageSHA1 = (:imageSHA1) ")
    suspend fun find(imageSHA1: String): Int
}
