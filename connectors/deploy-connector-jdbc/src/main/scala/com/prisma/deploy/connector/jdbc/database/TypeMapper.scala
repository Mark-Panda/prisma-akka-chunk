package com.prisma.deploy.connector.jdbc.database

import com.prisma.shared.models.ScalarField
import com.prisma.shared.models.TypeIdentifier.TypeIdentifier
import org.jooq.DSLContext
import org.jooq.impl.DSL.name

trait TypeMapper {
  def rawSQLForField(field: ScalarField)(implicit dsl: DSLContext): String = {
    rawSQLFromParts(
      field.dbName,
      field.isRequired,
      field.typeIdentifier,
      field.isAutoGeneratedByDb
    )
  }

  def rawSQLForFieldWithoutRequired(field: ScalarField)(implicit dsl: DSLContext): String = {
    rawSQLFromParts(
      field.dbName,
      isRequired = false,
      field.typeIdentifier,
      field.isAutoGeneratedByDb
    )
  }

  def rawSQLToMakeOldFieldOptional(oldName: String, oldTypeIdentifier: TypeIdentifier)(implicit dsl: DSLContext): String = {
    rawSQLFromParts(oldName, isRequired = false, oldTypeIdentifier, isAutoGenerated = false)
  }

  def rawSQLFromParts(
      name: String,
      isRequired: Boolean,
      typeIdentifier: TypeIdentifier,
      isAutoGenerated: Boolean = false,
  )(implicit dsl: DSLContext): String

  def rawSqlTypeForScalarTypeIdentifier(t: TypeIdentifier): String

  def esc(n: String)(implicit dsl: DSLContext): String = dsl.render(name(n))
}