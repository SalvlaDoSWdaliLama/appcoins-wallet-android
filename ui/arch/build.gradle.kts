plugins {
  id("appcoins.android.library")
}

android {
  namespace = "com.appcoins.wallet.ui.arch"
}

dependencies {
//  implementation(project(":ui:commons"))
//  implementation(project(":core:utils"))
  implementation(project(":legacy:billing"))

  implementation(libs.androidx.navigation.ui)

  implementation(libs.bundles.rx)
  implementation(libs.rx.rxlifecyle)
  implementation(libs.bundles.androidx.lifecycle)

}
