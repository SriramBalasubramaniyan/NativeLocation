import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  static const platform =
      MethodChannel('com.example.methodchannel.methodchannel/native');

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  double lat = 0.0;
  double lng = 0.0;

  Future<void> _getNativeData() async {
    PermissionStatus status = await Permission.location.request();
    if (status.isGranted) {
      try {
        final result = await MyApp.platform.invokeMethod('getNativeData');
        final Map<String, dynamic> data = Map<String, dynamic>.from(result);
        print("get location");
        lat = data['Latitude'] ?? 0.0;
        lng = data['Longitude'] ?? 0.0;
        print('Native Data: Latitude = $lat, Longitude = $lng');
      } catch (e) {
        print("Failed to get native data: '$e'");
        await _getNativeData();
      }
    } else {
      print('Location permission not granted');
    }
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text("Flutter Method Channel Example")),
        body: Center(
          child: Column(
            children: [
              Text("Lat: $lat"),
              Text("Lng: $lng"),
              ElevatedButton(
                onPressed: _getNativeData,
                child: Text("Get Data"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
