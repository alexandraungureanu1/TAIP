import builtins
import uuid

from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, UniqueConstraint
from sqlalchemy.dialects.postgresql import UUID

from tools.common_utils import to_sha256

Base = builtins.Base


class User(builtins.database.Model):
    __tablename__ = 'users'

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    name = Column(String(100), nullable=False, unique=True)
    mail = Column(String(100), nullable=False, unique=True)
    password = Column(String(100), nullable=False)
    role = Column(String(20), nullable=False)

    def __init__(self, data):
        if isinstance(data, dict):
            self.name = data.get("name", "")
            self.mail = data.get("mail", "")
            self.password = to_sha256(data.get("password", ""))
            self.role = data.get("role", "")

    def __repr__(self):
        return {
            "id": str(self.id),
            "name": self.name
        }


class Template(builtins.database.Model):
    __tablename__ = 'templates'

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    name = Column(String(100), nullable=False, unique=True)

    def __init__(self, data):
        if isinstance(data, dict):
            self.name = data.get("name", "")

    def __repr__(self):
        return {
            "id": self.id,
            "name": self.name
        }


class Page(builtins.database.Model):
    __tablename__ = 'pages'

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    template_id = Column(UUID(as_uuid=True), ForeignKey("templates.id", ondelete='CASCADE'), nullable=False,
                         default=uuid.uuid4)

    name = Column(String(100), nullable=False)
    image = Column(String(100))
    image_processed = Column(String(100))
    image_processed_low = Column(String(100))
    preview = Column(String(100))

    __table_args__ = (
        UniqueConstraint(template_id, name),
    )

    def __init__(self, data):
        if isinstance(data, dict):
            self.template_id = data.get("template_id", None)
            self.name = data.get("name", "")

    def clone(self, data):
        if isinstance(data, dict):
            self.name = data.get("name", "")

    def __repr__(self):
        return {
            "id": self.id,
            "template_id": self.template_id,
            "name": self.name,
            "preview": self.preview
        }


class Field(builtins.database.Model):
    __tablename__ = 'fields'

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    page_id = Column(UUID(as_uuid=True), ForeignKey("pages.id", ondelete='CASCADE'), nullable=False, default=uuid.uuid4)

    name = Column(String(100), nullable=False)
    sensitive = Column(Boolean(), nullable=False)
    category = Column(String(100), nullable=False)

    p1 = Column(Integer(), nullable=False)
    p2 = Column(Integer(), nullable=False)
    p3 = Column(Integer(), nullable=False)
    p4 = Column(Integer(), nullable=False)

    __table_args__ = (
        UniqueConstraint(page_id, name),
    )

    def __init__(self, data):
        if isinstance(data, dict):
            self.page_id = data.get("page_id", None)
            self.name = data.get("name", "")
            self.sensitive = data.get("sensitive", "")
            self.category = data.get("category", "")

            self.p1 = data.get("p1", None)
            self.p2 = data.get("p2", None)
            self.p3 = data.get("p3", None)
            self.p4 = data.get("p4", None)

    def clone(self, data):
        if isinstance(data, dict):
            self.name = data.get("name", "")
            self.sensitive = data.get("sensitive", "")
            self.category = data.get("category", "")

            self.p1 = data.get("p1", None)
            self.p2 = data.get("p2", None)
            self.p3 = data.get("p3", None)
            self.p4 = data.get("p4", None)

    def __repr__(self):
        return {
            "id": self.id,
            "page_id": self.page_id,
            "name": self.name,
            "sensitive": self.sensitive,
            "category": self.category,
            "p1": self.p1,
            "p2": self.p2,
            "p3": self.p3,
            "p4": self.p4
        }
