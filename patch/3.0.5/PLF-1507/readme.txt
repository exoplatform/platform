h1. Preamble {anchor:id="Bonita.Migration.Preamble"}

eXo Platform 3 provides a set of tools to migrate the system data from your BOS-5.5 standalone instance to your new eXo Platform Bonita instance.

So the entire old package will not be used, but it serves as a backup of the old system.

h1. Requirements {anchor:id="Bonita.Migration.Requirements"}

* BOS-5.5 standalone server

* Platform + Bonita server

In this document, *<BOS-Server>* and *<PLF-Server>* stand for the BOS-5.5 standalone server and Platform + Bonita server respectively.

h1. Install the migration libraries  {anchor:id="Bonita.Migration.Install_migration_libraries}

_This section provides step-by-step instructions for the backup procedure from a running BOS 5.5 standalone to Platform + Bonita._

*1.* Stop the running BOS-Server.

*2.* Copy the the BOS Migration Tool library, which is located in _/bos/lib/exo.platform.migration.bos.tool-<VERSION>.jar_, into the XCMIS lib directory of BOS-Server.

*3.* Restart the BOS-Server. In the log file (for example, <BOS-Server>/logs/catalina.out), you should see a message indicating that the backup service is started.

{code}
BOSBackupService: BOS Backup Service started
{code}

*4.* Access the server via _JMX_. In this example, _jconsole_ is used as a JMX client.

*5.* Select *exo > platform > bonita-ext > BOSBackupService* MBeans.
The backup is then proceeded in the selected location; that is, */exo-work/BOS-5.5/backup/*.

*6.* Fill the _backupLocation_ field with appropriate value: */exo-work/BOS-5.5/backup/cmis1* in the _JMX_ client.

!bos/jconsole1.png!

*7.* Select *doBackup*. The *Method successfully invoked* message appears.

!bos/backup-ok.png!

In the log file (for example, <BOS-Server>/logs/catalina.out), you will see a message indicating the successfull backup.

{code}
BOSBackupService: Starting the backup operation.. Repository: db1
BOSBackupService: Full backup succeeded: /exo-work/BOS-5.5/backup/system/db1_system-20110608_061330.0
BOSBackupService: Full backup succeeded: /exo-work/BOS-5.5/backup/cmis1/db1_cmis1-20110608_061330.0
{code}

After completing the backup procedure, go to the restore operation from the PLF-Server.

*8.* Copy the whole backup folder from */exo-work/BOS-5.5/backup/* to <PLF-Server>/temp.

*9.* Use the configuration archive located in _/bos/config/exo.platform.migration.bos.config-<VERSION>.zip_ and extract it in "<PLF-Server>/gatein/conf".

*10.* Start your server. You will see that the workspaces are well initialized from the restore files.

{code}
Jun 8, 2011 12:48:39 PM org.exoplatform.services.jcr.impl.core.BackupWorkspaceInitializer initWorkspace
INFO: Workspace system restored from file <PLF-Server>/bin/../temp/backup/system/db1_system-20110608_061330.0 in 0.269sec
Jun 8, 2011 12:48:39 PM org.exoplatform.services.jcr.impl.core.BackupWorkspaceInitializer initWorkspace
INFO: Workspace cmis1 restored from file <PLF-Server>/bin/../temp/backup/cmis1/db1_cmis1-20110608_061330.0 in 0.181sec
{code}
