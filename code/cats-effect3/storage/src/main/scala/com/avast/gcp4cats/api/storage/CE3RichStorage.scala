package com.avast.gcp4cats.api.storage

import cats.effect.Sync
import com.avast.gcp4cats.ce3.common.FS2Utils
import com.google.cloud.storage.{BlobId, BlobInfo}
import fs2.{Pipe, Stream}

import java.nio.channels.Channels

class CE3RichStorage[F[_]: Sync](storage: GCSStorage, fs2Utils: FS2Utils)
    extends CE3Storage[F](storage, fs2Utils)
    with RichStorage[F] {

  override def writerSink(blobInfo: BlobInfo): Pipe[F, Byte, Nothing] = { s =>
    Stream.resource(writer(blobInfo)).flatMap { r =>
      fs2.io
        .writeOutputStream(F.delay(Channels.newOutputStream(r)), false)
        .apply(s)
    }

  }

  override def readerSource(blobId: BlobId): Stream[F, Byte] = {
    Stream.resource(reader(blobId)).flatMap { r =>
      fs2.io.readInputStream(
        F.delay(Channels.newInputStream(r)),
        1024,
        closeAfterUse = false
      )
    }
  }
}
