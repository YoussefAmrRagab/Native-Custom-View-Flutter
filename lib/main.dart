import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    double dpValue = 200.0;
    double devicePixelRatio = MediaQuery.devicePixelRatioOf(context);
    double logicalPixels = dpValue / devicePixelRatio;
    double x = 3;
    return MaterialApp(
      theme: ThemeData(useMaterial3: false),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Custom Fan Controller'),
        ),
        body: Center(
          child: SizedBox(
            width: logicalPixels * x,
            height: logicalPixels * x,
            child: CustomFanControllerPlugin().build(context),
          ),
        ),
      ),
    );
  }
}

class CustomFanControllerPlugin {
  Widget build(BuildContext context) {
    // This is used in the platform side to register the view.
    const String viewType = 'native_dial_view';
    // Pass parameters to the platform side.
    final Map<String, dynamic> creationParams = <String, dynamic>{};

    return AndroidView(
      viewType: viewType,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}
