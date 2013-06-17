#!/bin/bash
# Original Developer: Rupendra Bandyopadhyay
# Company: Pivotal Inc.
# Publish Date: 06/16/2013

bindir=$(dirname $0)
java -jar ${bindir}/../target/sqltool.jar --inlineRc=url=jdbc:hsqldb:file:${bindir}/../mydb/db;shutdown=true,user=SA
