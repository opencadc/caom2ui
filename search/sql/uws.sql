CREATE SCHEMA IF NOT EXISTS uws;

CREATE TABLE "uws"."job"
(
   jobid varchar(16) PRIMARY KEY NOT NULL,
   runid text,
   ownerid int,
   executionphase varchar(16) NOT NULL,
   executionduration bigint NOT NULL,
   destructiontime timestamp,
   quote timestamp,
   creationtime timestamp NOT NULL,
   starttime timestamp,
   endtime timestamp,
   error_summarymessage text,
   error_type varchar(16),
   error_documenturl text,
   requestpath text,
   remoteip text,
   jobinfo_content text,
   jobinfo_contenttype text,
   jobinfo_valid smallint,
   deletedbyuser smallint DEFAULT 0,
   lastmodified timestamp NOT NULL
)
;
CREATE TABLE "uws"."jobdetail"
(
   jobid varchar(16) NOT NULL,
   type char(1) NOT NULL,
   name text NOT NULL,
   value text
)
;
CREATE INDEX uws_jobindex_creationtime ON "uws"."job"(creationtime)
;
CREATE INDEX uws_jobindex_ownerid ON "uws"."job"(ownerid)
;
CREATE UNIQUE INDEX job_pkey1 ON "uws"."job"(jobid)
;
ALTER TABLE "uws"."jobdetail"
ADD CONSTRAINT jobdetail_jobid_fkey
FOREIGN KEY (jobid)
REFERENCES "uws"."job"(jobid)
;
CREATE INDEX uws_param_i1 ON "uws"."jobdetail"(jobid)
;
