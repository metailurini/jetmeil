package org.metailurini.jetmeil

import junit.framework.TestCase
import org.metailurini.jetmeil.adapter.GitCommander
import org.metailurini.jetmeil.adapter.repository.BookmarkRepository
import org.mockito.Mockito
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class MainKtTest : TestCase() {

    fun testGetListProjectsRefreshProjects() {
        val bookmarkRepo = Mockito.mock<BookmarkRepository>()
        val listProject = listOf(Project(1, "test", "github.com/test"))
        val main = Main

        main.projects = listOf()
        main.bookmarkRepo = bookmarkRepo

        whenever(bookmarkRepo.getProjects()).thenReturn(listProject)

        getListProjects()
        verify(bookmarkRepo, Mockito.times(1)).getProjects()
        assertEquals(Main.projects, listProject)
    }

    fun testGetListProjectsCacheProjects() {
        val bookmarkRepo = Mockito.mock<BookmarkRepository>()
        val listProject = listOf(Project(1, "test", "github.com/test"))

        val main = Main
        main.projects = listProject
        main.bookmarkRepo = bookmarkRepo

        getListProjects()
        verifyNoInteractions(bookmarkRepo)
        assertEquals(Main.projects, listProject)
    }

    fun testUpsertProjectWithExitingProject() {
        val project = Mockito.mock<com.intellij.openapi.project.Project>()
        val bookmarkRepo = Mockito.mock<BookmarkRepository>()
        val gitter = Mockito.mock<GitCommander>()
        val listProject1 = listOf(Project(1, "test", "github.com"))
        val listProject2 = listOf(Project(2, "test2", "github.com/2"))

        val main = Main
        main.projects = listProject1
        main.bookmarkRepo = bookmarkRepo
        main.gitter = gitter


        whenever(project.basePath).thenReturn("/test")
        whenever(gitter.getRemoteURL("/test")).thenReturn("github.com/test")
        whenever(bookmarkRepo.getProjectsByPath("/test")).thenReturn(listProject2)

        upsertProject(project)

        verify(bookmarkRepo, Mockito.times(1)).getProjectsByPath("/test")
        verify(bookmarkRepo, Mockito.times(1)).UpsertProject(2, "/test", "github.com/test")
        assertEquals(Main.projects, listOf<Project>())
    }

    fun testUpsertProjectWithNewProject() {
        val project = Mockito.mock<com.intellij.openapi.project.Project>()
        val bookmarkRepo = Mockito.mock<BookmarkRepository>()
        val gitter = Mockito.mock<GitCommander>()
        val listProject1 = listOf(Project(1, "test", "github.com"))

        val main = Main
        main.projects = listProject1
        main.bookmarkRepo = bookmarkRepo
        main.gitter = gitter


        whenever(project.basePath).thenReturn("/test")
        whenever(gitter.getRemoteURL("/test")).thenReturn("github.com/test")
        whenever(bookmarkRepo.getProjectsByPath("/test")).thenReturn(listOf())

        upsertProject(project)

        verify(bookmarkRepo, Mockito.times(1)).getProjectsByPath("/test")
        verify(bookmarkRepo, Mockito.times(1)).UpsertProject(null, "/test", "github.com/test")
        assertEquals(Main.projects, listOf<Project>())
    }
}