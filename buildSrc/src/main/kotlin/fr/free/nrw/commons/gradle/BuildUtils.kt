package fr.free.nrw.commons.gradle

import org.eclipse.jgit.api.Git
import java.io.File

fun currentBranch(projectDir: File) : String =
    Git.open(projectDir).repository.branch

fun currentSha(projectDir: File): String =
    Git.open(projectDir).repository.exactRef("HEAD").objectId.abbreviate(9).name()