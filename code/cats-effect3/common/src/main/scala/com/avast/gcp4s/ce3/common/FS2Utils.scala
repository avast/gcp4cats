package com.avast.gcp4s.ce3.common

import cats.effect.Sync
import cats.implicits.toFunctorOps
import com.google.api.gax.paging.Page
import fs2.{Chunk, Stream}

import scala.jdk.CollectionConverters.IterableHasAsScala

trait FS2Utils {
  def streamPage[F[_]: Sync, A](page: Page[A]): Stream[F, A] = {
    Stream.unfoldChunkEval(page)(unfoldPageStep[F, A])
  }

  private def unfoldPageStep[F[_]: Sync, A](
      current: Page[A]
  ): F[Option[(Chunk[A], Page[A])]] = {
    if (current == null) {
      Sync[F].delay(None)
    } else {
      val chunk = Chunk.iterable(current.getValues.asScala)
      Sync[F]
        .interruptible(current.getNextPage)
        .map(next => Some(chunk -> next))
    }
  }

}
