package com.avast.gcp4s.api.storage

import cats.effect.{Resource, Sync}
import com.avast.gcp4s.ce3.common.FS2Utils
import com.google.api.gax.paging.Page
import com.google.cloud.storage.Storage.BlobSourceOption
import com.google.cloud.{ReadChannel, WriteChannel}
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Bucket}
import com.google.cloud.storage.Storage.{BlobListOption, BucketListOption}
import fs2.Stream

import java.nio.file.Path

class CE3Storage[F[_]: Sync](storage: GCSStorage, fs2Utils: FS2Utils)
    extends Storage[F] {
  protected val F: Sync[F] = Sync[F]

  override def list(opts: BucketListOption*): Stream[F, Bucket] = {
    blockingAsStream(storage.list(opts: _*))
  }

  override def list(bucket: String, opts: BlobListOption*): Stream[F, Blob] = {
    blockingAsStream(storage.list(bucket, opts: _*))
  }

  override def writer(blobInfo: BlobInfo): Resource[F, WriteChannel] = {
    Resource.eval(F.interruptibleMany(storage.writer(blobInfo)))
  }

  private def blockingAsStream[A](thunk: => Page[A]): Stream[F, A] = {
    Stream.eval(F.blocking(thunk)).flatMap(fs2Utils.streamPage[F, A])
  }

  override def reader(blobId: BlobId): Resource[F, ReadChannel] = {
    Resource.eval(F.blocking(storage.reader(blobId)))
  }

  override def downloadTo(
      blobId: BlobId,
      path: Path,
      options: BlobSourceOption*
  ): F[Unit] = {
    F.interruptible(storage.downloadTo(blobId, path, options: _*))
  }
}
