package com.avast.gcp4s.api.storage

import cats.effect.Resource
import com.google.cloud.{ReadChannel, WriteChannel}
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Bucket}
import com.google.cloud.storage.Storage.{BlobListOption, BucketListOption}
import fs2.Stream

trait Storage[F[_]] {
  def list(opts: BucketListOption*): Stream[F, Bucket]

  def list(bucket: String, opts: BlobListOption*): Stream[F, Blob]

  def writer(blobInfo: BlobInfo): Resource[F, WriteChannel]

  def reader(blobId: BlobId): Resource[F, ReadChannel]

}
