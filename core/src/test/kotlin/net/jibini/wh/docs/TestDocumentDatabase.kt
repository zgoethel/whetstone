package net.jibini.wh.docs

import net.jibini.wh.annotation.AugmentBean
import net.jibini.wh.data.Database
import org.springframework.stereotype.Repository

@AugmentBean(TestDocumentAugment::class)
@Repository
class TestDocumentDatabase : Database()
{

}