package com.squareup.backfila.service

import misk.hibernate.DbTimestampedEntity
import misk.hibernate.DbUnsharded
import misk.hibernate.Id
import misk.hibernate.JsonColumn
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Table
import javax.persistence.Version

/**
 * Tracks the state of a created backfill.
 */
@Entity
@Table(name = "backfill_runs")
class DbBackfillRun() : DbUnsharded<DbBackfillRun>, DbTimestampedEntity {
  @javax.persistence.Id
  @GeneratedValue
  override lateinit var id: Id<DbBackfillRun>

  @Column(nullable = false)
  lateinit var service_id: Id<DbService>

  /** Immutably stores the data configured by the client service for this backfill. */
  @Column(nullable = false)
  lateinit var registered_backfill_id: Id<DbRegisteredBackfill>

  @Column
  var pipeline_target_backfill_id: Id<DbRegisteredBackfill>? = null

  @Column
  override lateinit var created_at: Instant

  @Column
  override lateinit var updated_at: Instant

  @Column(nullable = false) @Version
  var version: Long = 0

  @Column(nullable = false) @Enumerated(EnumType.STRING)
  lateinit var state: BackfillState

  @Column
  var created_by_user: String? = null

  @Column
  var approved_by_user: String? = null

  var approved_at: Instant? = null

  @Column(nullable = false)
  var scan_size: Long = 0

  @Column(nullable = false)
  var batch_size: Long = 0

  @Column(nullable = false)
  var num_threads: Long = 0

  // TODO(mgersh): denormalize into a 1,n table
  @JsonColumn @Column(columnDefinition = "mediumtext")
  var parameter_map: Map<String, String>? = null

  constructor(
    service_id: Id<DbService>,
    registered_backfill_id: Id<DbRegisteredBackfill>,
    parameter_map: Map<String, String>,
    created_by_user: String?,
    scan_size: Long,
    batch_size: Long,
    num_threads: Long
  ) : this() {
    this.service_id = service_id
    this.registered_backfill_id = registered_backfill_id
    this.parameter_map = parameter_map
    this.state = BackfillState.PAUSED
    this.created_by_user = created_by_user
    this.scan_size = scan_size
    this.batch_size = batch_size
    this.num_threads = num_threads
  }
}

// TODO move me out
enum class BackfillState {
  PAUSED,
  RUNNING,
  COMPLETE
}
