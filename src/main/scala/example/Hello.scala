package example

import cats.effect.{Async, Resource, Sync}
import cats.implicits.toFunctorOps
import com.google.api.gax.paging.Page
import com.google.cloud.WriteChannel
import com.google.cloud.storage.Storage.{BlobGetOption, BlobListOption, BucketListOption}
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Bucket, StorageBatch, StorageBatchResult, Storage => GCSStorage}
import fs2.{Chunk, Pipe, Stream}

import java.nio.ByteBuffer
import scala.jdk.CollectionConverters.IterableHasAsScala


trait Storage[F[_]] {
  def list(opts: BucketListOption*): Stream[F,Bucket]
  def list(bucket: String, opts: BlobListOption*): Stream[F, Blob]
  def writer(blobInfo: BlobInfo): Resource[F, WriteChannel]
}


trait FS2Utils {
  def streamPage[F[_]: Sync,A](page: Page[A]): Stream[F, A] = {
    Stream.unfoldChunkEval(page)(unfoldPageStep[F, A])
  }

  private def unfoldPageStep[F[_]: Sync,A](current: Page[A]): F[Option[(Chunk[A],Page[A])]] = {
      if (current == null) {
        Sync[F].delay(None)
      } else {
        val chunk = Chunk.iterable(current.getValues.asScala)
        Sync[F].blocking(current.getNextPage).map(next => Some(chunk -> next))
      }
  }

}

class CE3Storage[F[_]: Sync](storage: GCSStorage, fs2Utils: FS2Utils) extends Storage[F] {
  protected val F: Sync[F] = Sync[F]

  override def list(opts: BucketListOption*): Stream[F, Bucket] = {
    blockingAsStream(storage.list(opts: _*))
  }

  override def list(bucket: String, opts: BlobListOption*): Stream[F, Blob] = {
    blockingAsStream(storage.list(bucket, opts: _*))
  }

  override def writer(blobInfo: BlobInfo): Resource[F, WriteChannel] = {
    Resource.eval(F.blocking(storage.writer(blobInfo)))
  }

  private def blockingAsStream[A](thunk: => Page[A]): Stream[F, A] = {
    Stream.eval(F.blocking(thunk)).flatMap(fs2Utils.streamPage[F, A])
  }
}

trait RichStorage[F[_]] extends Storage[F] {
  def writerSink(blobInfo: BlobInfo): Pipe[F, ByteBuffer, Unit]
}

class CE3RichStorage[F[_]: Sync](storage: GCSStorage, fs2Utils: FS2Utils) extends CE3Storage[F](storage, fs2Utils) with RichStorage[F] {

  override def writerSink(blobInfo: BlobInfo): Pipe[F, ByteBuffer, Unit] = { s =>
    Stream.resource(writer(blobInfo)).flatMap(channel =>
      s.evalMap(data => F.blocking(channel.write(data)))
    )
  }
}


class CE3Batch[F[_]: Async](batch: StorageBatch) {
  private val F: Async[F] = Async[F]

  def get(blobId: BlobId, opts: BlobGetOption*): StorageBatchResult[Blob] = {
    batch.get(blobId, opts: _*)
  }

  def submit(): F[Unit] = {
    F.blocking(batch.submit())
  }

}

object Test {
  private val storage: com.google.cloud.storage.Storage = ???
}

