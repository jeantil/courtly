s3credentialsFile := {
  val home = System.getenv("HOME")
  val creds =file(s"$home/.credentials.properties")
  if(creds.exists()) Some(creds.getAbsolutePath) else None
}

