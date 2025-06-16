import SwiftUI
import ComposeApp

struct ContentView: View {
    var body: some View {
        ComposeUIViewController()
            .ignoresSafeArea(.all)
    }
}

struct ComposeUIViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

#Preview {
    ContentView()
}