package com.softwaremill.codebrag.service.commits.jgit

import org.scalatest.{BeforeAndAfter, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import org.eclipse.jgit.util.FileUtils
import com.softwaremill.codebrag.service.commits.{GitHubRepoData, TestCodebragAndRepositoryConfig}

class InternalGitDirTreeSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  var dirTree: InternalDirTree = _

  before {
    dirTree = new InternalDirTree(TestCodebragAndRepositoryConfig)
  }

  after {
    deleteRootDirectoryRecursively()
  }

  behavior of "InternalGitDirTree"

  it should "not contain a repository if root directory does not exist" in {
    // given no root directory
    dirTree.containsRepo(new GitHubRepoData("someOwner", "someRepo", "refs/heads/master", "token")) should be(false)
  }


  it should "not contain a repository if root directory exists but there's no repo directory" in {
    // given
    givenExistingRootDirectory()
    // when
    dirTree.containsRepo(new GitHubRepoData("someOwner", "someRepo", "refs/heads/master", "token")) should be(false)
  }

  it should "contain a repository if its directory exists" in {
    // given
    givenExistingRootDirectory()
    givenExistingRepository("codebrag")
    // when
    dirTree.containsRepo(new GitHubRepoData("softwaremill", "codebrag", "refs/heads/master", "token")) should be(true)
  }

  def deleteRootDirectoryRecursively() {
    FileUtils.delete(dirTree.root.toFile, FileUtils.RECURSIVE | FileUtils.SKIP_MISSING)
  }

  def givenExistingRootDirectory() {
    FileUtils.mkdirs(dirTree.root.toFile)
  }

  def givenExistingRepository(repository: String) {
    FileUtils.mkdirs(dirTree.root.resolve(repository).toFile)
  }
}
