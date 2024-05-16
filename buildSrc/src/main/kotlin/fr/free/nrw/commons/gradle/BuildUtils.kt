package fr.free.nrw.commons.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import java.io.File

// Return equivalent of 'git rev-parse --abbrev-ref HEAD'
// inline this convenience method once the gradle build file is in kotlin!
fun currentBranch(projectDir: File): String = projectDir.branch

// Return equivalent of 'git rev-parse --short HEAD'
// inline this convenience method once the gradle build file is in kotlin!
fun currentSha(projectDir: File): String = projectDir.head.shortSha

private val File.branch : String get() = Git.open(this).repository.branch
private val File.head: Ref get() = Git.open(this).repository.exactRef("HEAD")
private val Ref.shortSha get() = objectId.abbreviate(9).name()