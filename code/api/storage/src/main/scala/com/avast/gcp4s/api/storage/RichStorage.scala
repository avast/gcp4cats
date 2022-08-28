package com.avast.gcp4s.api.storage

import com.google.cloud.storage.{BlobId, BlobInfo}
import fs2.{Stream, Pipe}

trait RichStorage[F[_]] extends Storage[F] {
  def writerSink(blobInfo: BlobInfo): Pipe[F, Byte, Unit]
  def readerSource(blobId: BlobId): Stream[F, Byte]
}
