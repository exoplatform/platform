alter table JBPM_ACTION drop constraint FK_ACTION_REFACT
go
alter table JBPM_ACTION drop constraint FK_CRTETIMERACT_TA
go
alter table JBPM_ACTION drop constraint FK_ACTION_PROCDEF
go
alter table JBPM_ACTION drop constraint FK_ACTION_EVENT
go
alter table JBPM_ACTION drop constraint FK_ACTION_ACTNDEL
go
alter table JBPM_ACTION drop constraint FK_ACTION_EXPTHDL
go
alter table JBPM_BYTEARRAY drop constraint FK_BYTEARR_FILDEF
go
alter table JBPM_BYTEBLOCK drop constraint FK_BYTEBLOCK_FILE
go
alter table JBPM_COMMENT drop constraint FK_COMMENT_TOKEN
go
alter table JBPM_COMMENT drop constraint FK_COMMENT_TSK
go
alter table JBPM_DECISIONCONDITIONS drop constraint FK_DECCOND_DEC
go
alter table JBPM_DELEGATION drop constraint FK_DELEGATION_PRCD
go
alter table JBPM_EVENT drop constraint FK_EVENT_PROCDEF
go
alter table JBPM_EVENT drop constraint FK_EVENT_TRANS
go
alter table JBPM_EVENT drop constraint FK_EVENT_NODE
go
alter table JBPM_EVENT drop constraint FK_EVENT_TASK
go
alter table JBPM_ID_GROUP drop constraint FK_ID_GRP_PARENT
go
alter table JBPM_ID_MEMBERSHIP drop constraint FK_ID_MEMSHIP_GRP
go
alter table JBPM_ID_MEMBERSHIP drop constraint FK_ID_MEMSHIP_USR
go
alter table JBPM_JOB drop constraint FK_JOB_PRINST
go
alter table JBPM_JOB drop constraint FK_JOB_ACTION
go
alter table JBPM_JOB drop constraint FK_JOB_TOKEN
go
alter table JBPM_JOB drop constraint FK_JOB_NODE
go
alter table JBPM_JOB drop constraint FK_JOB_TSKINST
go
alter table JBPM_LOG drop constraint FK_LOG_SOURCENODE
go
alter table JBPM_LOG drop constraint FK_LOG_DESTNODE
go
alter table JBPM_LOG drop constraint FK_LOG_TOKEN
go
alter table JBPM_LOG drop constraint FK_LOG_TRANSITION
go
alter table JBPM_LOG drop constraint FK_LOG_TASKINST
go
alter table JBPM_LOG drop constraint FK_LOG_CHILDTOKEN
go
alter table JBPM_LOG drop constraint FK_LOG_OLDBYTES
go
alter table JBPM_LOG drop constraint FK_LOG_SWIMINST
go
alter table JBPM_LOG drop constraint FK_LOG_NEWBYTES
go
alter table JBPM_LOG drop constraint FK_LOG_ACTION
go
alter table JBPM_LOG drop constraint FK_LOG_VARINST
go
alter table JBPM_LOG drop constraint FK_LOG_NODE
go
alter table JBPM_LOG drop constraint FK_LOG_PARENT
go
alter table JBPM_MODULEDEFINITION drop constraint FK_MODDEF_PROCDEF
go
alter table JBPM_MODULEDEFINITION drop constraint FK_TSKDEF_START
go
alter table JBPM_MODULEINSTANCE drop constraint FK_MODINST_PRCINST
go
alter table JBPM_MODULEINSTANCE drop constraint FK_TASKMGTINST_TMD
go
alter table JBPM_NODE drop constraint FK_DECISION_DELEG
go
alter table JBPM_NODE drop constraint FK_NODE_PROCDEF
go
alter table JBPM_NODE drop constraint FK_NODE_ACTION
go
alter table JBPM_NODE drop constraint FK_PROCST_SBPRCDEF
go
alter table JBPM_NODE drop constraint FK_NODE_SCRIPT
go
alter table JBPM_NODE drop constraint FK_NODE_SUPERSTATE
go
alter table JBPM_POOLEDACTOR drop constraint FK_POOLEDACTOR_SLI
go
alter table JBPM_PROCESSDEFINITION drop constraint FK_PROCDEF_STRTSTA
go
alter table JBPM_PROCESSINSTANCE drop constraint FK_PROCIN_PROCDEF
go
alter table JBPM_PROCESSINSTANCE drop constraint FK_PROCIN_ROOTTKN
go
alter table JBPM_PROCESSINSTANCE drop constraint FK_PROCIN_SPROCTKN
go
alter table JBPM_RUNTIMEACTION drop constraint FK_RTACTN_PROCINST
go
alter table JBPM_RUNTIMEACTION drop constraint FK_RTACTN_ACTION
go
alter table JBPM_SWIMLANE drop constraint FK_SWL_ASSDEL
go
alter table JBPM_SWIMLANE drop constraint FK_SWL_TSKMGMTDEF
go
alter table JBPM_SWIMLANEINSTANCE drop constraint FK_SWIMLANEINST_TM
go
alter table JBPM_SWIMLANEINSTANCE drop constraint FK_SWIMLANEINST_SL
go
alter table JBPM_TASK drop constraint FK_TASK_STARTST
go
alter table JBPM_TASK drop constraint FK_TASK_PROCDEF
go
alter table JBPM_TASK drop constraint FK_TASK_ASSDEL
go
alter table JBPM_TASK drop constraint FK_TASK_SWIMLANE
go
alter table JBPM_TASK drop constraint FK_TASK_TASKNODE
go
alter table JBPM_TASK drop constraint FK_TASK_TASKMGTDEF
go
alter table JBPM_TASK drop constraint FK_TSK_TSKCTRL
go
alter table JBPM_TASKACTORPOOL drop constraint FK_TASKACTPL_TSKI
go
alter table JBPM_TASKACTORPOOL drop constraint FK_TSKACTPOL_PLACT
go
alter table JBPM_TASKCONTROLLER drop constraint FK_TSKCTRL_DELEG
go
alter table JBPM_TASKINSTANCE drop constraint FK_TSKINS_PRCINS
go
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_TMINST
go
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_TOKEN
go
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_SLINST
go
alter table JBPM_TASKINSTANCE drop constraint FK_TASKINST_TASK
go
alter table JBPM_TOKEN drop constraint FK_TOKEN_SUBPI
go
alter table JBPM_TOKEN drop constraint FK_TOKEN_PROCINST
go
alter table JBPM_TOKEN drop constraint FK_TOKEN_NODE
go
alter table JBPM_TOKEN drop constraint FK_TOKEN_PARENT
go
alter table JBPM_TOKENVARIABLEMAP drop constraint FK_TKVARMAP_TOKEN
go
alter table JBPM_TOKENVARIABLEMAP drop constraint FK_TKVARMAP_CTXT
go
alter table JBPM_TRANSITION drop constraint FK_TRANSITION_FROM
go
alter table JBPM_TRANSITION drop constraint FK_TRANS_PROCDEF
go
alter table JBPM_TRANSITION drop constraint FK_TRANSITION_TO
go
alter table JBPM_VARIABLEACCESS drop constraint FK_VARACC_PROCST
go
alter table JBPM_VARIABLEACCESS drop constraint FK_VARACC_SCRIPT
go
alter table JBPM_VARIABLEACCESS drop constraint FK_VARACC_TSKCTRL
go
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VARINST_PRCINST
go
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VARINST_TKVARMP
go
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VARINST_TK
go
alter table JBPM_VARIABLEINSTANCE drop constraint FK_BYTEINST_ARRAY
go
alter table JBPM_VARIABLEINSTANCE drop constraint FK_VAR_TSKINST
go
drop table JBPM_ACTION
go
drop table JBPM_BYTEARRAY
go
drop table JBPM_BYTEBLOCK
go
drop table JBPM_COMMENT
go
drop table JBPM_DECISIONCONDITIONS
go
drop table JBPM_DELEGATION
go
drop table JBPM_EVENT
go
drop table JBPM_EXCEPTIONHANDLER
go
drop table JBPM_ID_GROUP
go
drop table JBPM_ID_MEMBERSHIP
go
drop table JBPM_ID_PERMISSIONS
go
drop table JBPM_ID_USER
go
drop table JBPM_JOB
go
drop table JBPM_LOG
go
drop table JBPM_MODULEDEFINITION
go
drop table JBPM_MODULEINSTANCE
go
drop table JBPM_NODE
go
drop table JBPM_POOLEDACTOR
go
drop table JBPM_PROCESSDEFINITION
go
drop table JBPM_PROCESSINSTANCE
go
drop table JBPM_RUNTIMEACTION
go
drop table JBPM_SWIMLANE
go
drop table JBPM_SWIMLANEINSTANCE
go
drop table JBPM_TASK
go
drop table JBPM_TASKACTORPOOL
go
drop table JBPM_TASKCONTROLLER
go
drop table JBPM_TASKINSTANCE
go
drop table JBPM_TOKEN
go
drop table JBPM_TOKENVARIABLEMAP
go
drop table JBPM_TRANSITION
go
drop table JBPM_VARIABLEACCESS
go
drop table JBPM_VARIABLEINSTANCE
go
