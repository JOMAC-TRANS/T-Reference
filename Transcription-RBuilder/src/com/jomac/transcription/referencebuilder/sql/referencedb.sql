CREATE TABLE schema_version
(
 version character varying(30),
 dbname character varying(30)
);

CREATE TABLE expiration
(
 expirationdate date,
 firstusagedate date
);

CREATE TABLE dictators
(
  dictatorid integer NOT NULL AUTO_INCREMENT,
  lastname character varying(60),
  firstname character varying(60),
  middlename character varying(30),
  CONSTRAINT pk_dictator PRIMARY KEY (dictatorid)
);

CREATE TABLE worktypes
(
  worktypeid integer NOT NULL  AUTO_INCREMENT,
  worktype character varying(30),
  CONSTRAINT pk_worktype PRIMARY KEY (worktypeid)
);

CREATE TABLE dictatorworktypes
(
 dictatorworktypeid integer NOT NULL AUTO_INCREMENT,
 dictatorid integer NOT NULL,
 worktypeid integer NOT NULL,
 CONSTRAINT pk_dictatorworktype PRIMARY KEY (dictatorworktypeid),
 CONSTRAINT fk_dictator FOREIGN KEY (dictatorid)
 REFERENCES dictators (dictatorid),
 CONSTRAINT fk_worktype_ FOREIGN KEY (worktypeid)
 REFERENCES worktypes (worktypeid)
);

CREATE TABLE documents
(
 documentid integer NOT NULL AUTO_INCREMENT,
 dictatorworktypeid integer NOT NULL,
 document character varying(50000),
 CONSTRAINT documents_pkey PRIMARY KEY (documentid),
 CONSTRAINT fk_dictatorworktype FOREIGN KEY (dictatorworktypeid)
 REFERENCES dictatorworktypes (dictatorworktypeid)
);

CREATE TABLE specifics
(
 specificid integer NOT NULL AUTO_INCREMENT,
 dictatorworktypeid integer,
 document character varying(10000),
 CONSTRAINT specifics_pkey PRIMARY KEY (specificid),
 CONSTRAINT fk_dictatorworktype_ FOREIGN KEY (dictatorworktypeid)
 REFERENCES dictatorworktypes (dictatorworktypeid)
 );