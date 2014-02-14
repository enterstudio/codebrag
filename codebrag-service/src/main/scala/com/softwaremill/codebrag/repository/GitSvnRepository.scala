package com.softwaremill.codebrag.repository

import org.apache.commons.lang3.SystemUtils
import scala.sys.process._
import java.nio.file.{Path, Paths}
import com.softwaremill.codebrag.repository.config.{RepoConfig, UserPassCredentials}

class GitSvnRepository(val repoConfig: RepoConfig) extends Repository {

  private val CommandBase = "git svn rebase --quiet"

  def pullChanges {
    try {
      runPullCommand
      logger.debug(s"Changes pulled succesfully")
    } catch {
      case e: Exception => throw new RuntimeException(s"Cannot pull changes for repo: ${repoConfig.repoLocation}", e)
    }
  }

  private def runPullCommand = {
    val repoPath = Paths.get(repoConfig.repoLocation)
    if(repoConfig.credentials.isDefined) {
      repoConfig.credentials.get match {
        case c: UserPassCredentials => runWithUserPassCredentials(repoPath, c)
        case _ => runWithNoCredentials(repoPath)
      }
    } else {
      runWithNoCredentials(repoPath)
    }
  }

  private def runWithUserPassCredentials(repoPath: Path, c: UserPassCredentials) {
    callOsCommand(s"echo ${c.pass}") #| Process(s"${CommandBase} --username ${c.user}", repoPath.toFile) !< ProcessLogger(logger info _)
    def callOsCommand(command: String) = if (SystemUtils.IS_OS_WINDOWS) s"cmd /c ${command}" else command
  }

  private def runWithNoCredentials(repoPath: Path) {
    Process(CommandBase, repoPath.toFile) !< ProcessLogger(logger info _)
  }


}