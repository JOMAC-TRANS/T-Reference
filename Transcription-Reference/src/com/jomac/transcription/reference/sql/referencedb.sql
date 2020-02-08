CREATE TABLE schema_version
(
    version character varying(30),
    dbname character varying(30)
);

CREATE TABLE activators
(
    activatorid integer NOT NULL AUTO_INCREMENT,
    fullname character varying(50),
    registrationid integer,
    plugins integer,
    expirationdate date,
    lastlogin date,
    activated boolean default false,
    CONSTRAINT pk_activator PRIMARY KEY (activatorid)
);