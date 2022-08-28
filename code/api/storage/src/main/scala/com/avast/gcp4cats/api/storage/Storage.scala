package com.avast.gcp4cats.api.storage

import cats.effect.Resource
import com.google.cloud.storage.Storage.{
  BlobListOption,
  BlobSourceOption,
  BucketListOption
}
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Bucket}
import com.google.cloud.{ReadChannel, WriteChannel}
import fs2.Stream

import java.nio.file.Path

trait Storage[F[_]] {
  def list(opts: BucketListOption*): Stream[F, Bucket]

  def list(bucket: String, opts: BlobListOption*): Stream[F, Blob]

  def writer(blobInfo: BlobInfo): Resource[F, WriteChannel]

  def reader(blobId: BlobId): Resource[F, ReadChannel]

  def downloadTo(
      blobId: BlobId,
      path: Path,
      options: BlobSourceOption*
  ): F[Unit]

}
